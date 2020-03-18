package cn.laoshini.game.example.tank.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import cn.laoshini.dk.annotation.FunctionDependent;
import cn.laoshini.dk.function.Func;
import cn.laoshini.dk.generator.id.IRoleIdGenerator;
import cn.laoshini.dk.generator.name.INameGenerator;
import cn.laoshini.game.example.tank.common.TankException;
import cn.laoshini.game.example.tank.constant.ForbidType;
import cn.laoshini.game.example.tank.constant.GameError;
import cn.laoshini.game.example.tank.constant.RoleState;
import cn.laoshini.game.example.tank.constant.TankConstants;
import cn.laoshini.game.example.tank.domain.TankPlayer;
import cn.laoshini.game.example.tank.entity.Account;
import cn.laoshini.game.example.tank.entity.Role;
import cn.laoshini.game.example.tank.manage.player.PlayerDataManager;
import cn.laoshini.game.example.tank.manage.room.RoomDataManager;
import cn.laoshini.game.example.tank.message.BeginGameReq;
import cn.laoshini.game.example.tank.message.BeginGameRes;
import cn.laoshini.game.example.tank.message.GetNamesRes;
import cn.laoshini.game.example.tank.message.HeartBeatReq;
import cn.laoshini.game.example.tank.message.HeartBeatRes;
import cn.laoshini.game.example.tank.message.RoleLoginReq;
import cn.laoshini.game.example.tank.message.RoleLoginRes;
import cn.laoshini.game.example.tank.util.TimeHelper;

import static cn.laoshini.game.example.tank.constant.GameError.*;

/**
 * @author fagarine
 */
@Service
public class PlayerServiceImpl implements IPlayerService {

    @Resource
    private PlayerDataManager playerDataManager;

    @Resource
    private RoomDataManager roomDataManager;

    @FunctionDependent
    private IRoleIdGenerator roleIdGenerator;

    @FunctionDependent(initMethod = "setNameGeneratorParams")
    private Func<INameGenerator> nameGenerator;

    private void setNameGeneratorParams() {
        nameGenerator.get().setNameLengthLimit(2);
    }

    @Override
    public String getUniqueId() throws TankException {
        String uid = UUID.randomUUID().toString().replace("-", "");
        while (playerDataManager.getAccountByDeviceNo(uid) != null) {
            uid = UUID.randomUUID().toString().replace("-", "");
        }

        return uid;
    }

    @Override
    public void beginGame(BeginGameReq req, TankPlayer player) throws TankException {
        Role role;
        Account account = playerDataManager.getAccountByDeviceNo(req.getDeviceNo());
        if (account != null) {
            TankPlayer origin = playerDataManager.getPlayerAccountId(account.getAccountId());
            if (origin != null) {
                origin.copyTo(player, req.getReconnect() != null && req.getReconnect());

                // 断开原连接
                if (origin.getSession() != null) {
                    origin.getSession().close();
                }
            } else {
                player.setAccount(account);
                playerDataManager.putPlayerByAccountId(account.getAccountId(), player);
            }

        } else {
            playerDataManager
                    .createPlayer(player, req.getPlatNo(), req.getDeviceNo(), req.getBaseVersion(), req.getVersion());
            account = player.getAccount();
        }

        checkAndUpdateAccountForbid(account);

        if (player.getRole() != null) {
            role = player.getRole();
        } else {
            long roleId = roleIdGenerator.nextRoleId(account.getPlatNo(), TankConstants.GAME_ID, req.getServerId());
            role = playerDataManager.createRole(account, roleId, getNewNick());
            player.setRole(role);
            playerDataManager.addPlayer(player);
        }

        List<String> names = RoleState.UNCREATED.getState() == role.getRoleState() ?
                getBatchNames() :
                Lists.newArrayList(role.getNick());
        player.sendRespMessage(BeginGameRes.MESSAGE_ID,
                BeginGameRes.builder().state(role.getRoleState()).names(names).serverTime(TimeHelper.getCurrentSecond())
                        .build());
    }

    private void checkAndUpdateAccountForbid(Account account) {
        int forbid = account.getForbid();
        if (ForbidType.NONE.getCode() != forbid) {
            if (ForbidType.FOREVER.getCode() == forbid) {
                throw new TankException(LOGIN_FORBID, "永久禁止登录, roleId:" + account.getRoleId());
            }

            Date now = new Date();
            if (now.compareTo(account.getLiftTime()) >= 0) {
                account.setForbid(ForbidType.NONE.getCode());
                account.setLiftTime(null);
                playerDataManager.updateAccount(account);
            } else {
                throw new TankException(LOGIN_FORBID, "未到解禁时间, roleId:" + account.getRoleId());
            }
        }
    }

    @Override
    public void getNames(TankPlayer player) throws TankException {
        player.sendRespMessage(GetNamesRes.MESSAGE_ID, GetNamesRes.builder().names(getBatchNames()).build());
    }

    @Override
    public void roleLogin(RoleLoginReq req, TankPlayer player) throws TankException {
        if (player.getAccount() == null || player.getRole() == null) {
            throw new TankException(ILLEGAL_REQUEST, "非法请求");
        }

        Role role = player.getRole();
        if (RoleState.LOCKED.getState() == role.getRoleState()) {
            throw new TankException(ROLE_LOCKED, "角色已被锁");
        } else if (RoleState.UNCREATED.getState() == role.getRoleState()) {
            role.setRoleState(RoleState.NORMAL.getState());
        }

        String nick = req.getNick();
        int portrait = req.getPortrait();
        if (!role.getNick().equals(nick) && !playerDataManager.isUnusedName(nick)) {
            throw new TankException(GameError.SAME_NICK, "玩家昵称已被使用:" + nick);
        }

        role.setNick(nick);
        role.setPortrait(portrait);
        role.setOnTime(new Date());
        playerDataManager.updateRole(role);
        playerDataManager.updateAccountOnLogin(player.getAccount(), role.getRoleId(), req.getServerId());

        player.setOnline(true);
        playerDataManager.addOnline(player);
        playerDataManager.addPlayer(player);

        RoleLoginRes res = RoleLoginRes.builder().roleId(role.getRoleId()).nick(nick).portrait(portrait).build();
        player.sendRespMessage(RoleLoginRes.MESSAGE_ID, res);
    }

    @Override
    public void roleLogout(TankPlayer player) throws TankException {
        if (!player.isOnline()) {
            return;
        }

        Date now = new Date();
        Role role = player.getRole();
        int nowSecond = TimeHelper.millsToSecond(now.getTime());
        role.setOffTime(now);
        role.setOlTime(nowSecond - TimeHelper.millsToSecond(role.getOnTime().getTime()));
        playerDataManager.updateRole(role);

        int roomId = player.logout();
        playerDataManager.removeOnline(player);

        if (roomId > 0) {
            roomDataManager.roleExitRoom(roomId, player);
        }
    }

    @Override
    public void heartBeat(HeartBeatReq req, TankPlayer player) throws TankException {
        // 如果需要，执行心跳检查

        // 返回消息
        player.sendRespMessage(HeartBeatRes.MESSAGE_ID,
                HeartBeatRes.builder().index(req.getIndex() + 1).serverTime(TimeHelper.getCurrentSecond()).build());
    }

    private List<String> getBatchNames() throws TankException {
        return nameGenerator.get().batchName(6);
    }

    private String getNewNick() {
        return nameGenerator.get().newName();
    }
}
