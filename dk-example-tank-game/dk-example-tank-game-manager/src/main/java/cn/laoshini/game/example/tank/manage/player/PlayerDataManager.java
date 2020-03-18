package cn.laoshini.game.example.tank.manage.player;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cn.laoshini.dk.util.CollectionUtil;
import cn.laoshini.game.example.tank.constant.ForbidType;
import cn.laoshini.game.example.tank.constant.RoleState;
import cn.laoshini.game.example.tank.constant.WhiteNameType;
import cn.laoshini.game.example.tank.domain.TankPlayer;
import cn.laoshini.game.example.tank.entity.Account;
import cn.laoshini.game.example.tank.entity.Role;

/**
 * @author fagarine
 */
@Component
public class PlayerDataManager {

    @Resource
    private AccountDao accountDao;

    private Map<String, Account> deviceNoToAccount = new ConcurrentHashMap<>();

    private Map<Integer, TankPlayer> accountIdToPlayer = new ConcurrentHashMap<>();

    private Map<Long, TankPlayer> roleIdToPlayer = new ConcurrentHashMap<>();

    private Map<String, TankPlayer> nickToPlayer = new ConcurrentHashMap<>();

    private Map<String, TankPlayer> onlinePlayer = new ConcurrentHashMap<>();

    public Account getAccountByDeviceNo(String deviceNo) {
        return deviceNoToAccount.get(deviceNo);
    }

    public TankPlayer getPlayerAccountId(Integer accountId) {
        return accountIdToPlayer.get(accountId);
    }

    public TankPlayer getPlayerByRoleId(Long roleId) {
        return roleIdToPlayer.get(roleId);
    }

    public TankPlayer getPlayerByNick(String nick) {
        return nickToPlayer.get(nick);
    }

    public void addPlayer(TankPlayer player) {
        roleIdToPlayer.put(player.getRoleId(), player);
        accountIdToPlayer.put(player.getAccountId(), player);
        nickToPlayer.put(player.getRole().getNick(), player);
    }

    public void putPlayerByAccountId(int accountId, TankPlayer player) {
        accountIdToPlayer.put(accountId, player);
    }

    public void addOnline(TankPlayer player) {
        onlinePlayer.put(player.getRole().getNick(), player);
    }

    public void removeOnline(TankPlayer player) {
        onlinePlayer.remove(player.getRole().getNick());
    }

    public TankPlayer getOnlinePlayer(String nick) {
        return onlinePlayer.get(nick);
    }

    public Map<String, TankPlayer> getAllOnlinePlayer() {
        return onlinePlayer;
    }

    public boolean isUnusedName(String nick) {
        return !onlinePlayer.containsKey(nick);
    }

    public void createPlayer(TankPlayer player, int platNo, String deviceNo, String baseVersion, String version) {
        Date now = new Date();
        Account account = new Account();
        account.setPlatNo(platNo);
        account.setChildNo(0);
        account.setPlatId(deviceNo);
        account.setDeviceNo(deviceNo);
        account.setServerId(0);
        account.setRoleId(0L);
        account.setWhiteName(WhiteNameType.NONE.getCode());
        account.setForbid(ForbidType.NONE.getCode());
        account.setLoginTime(now);
        account.setCreateTime(now);
        accountDao.insertAccount(account);

        deviceNoToAccount.put(account.getDeviceNo(), account);
        accountIdToPlayer.put(account.getAccountId(), player);

        player.setAccount(account);
    }

    public Role createRole(Account account, long roleId, String nick) {
        Role role = new Role();
        role.setRoleId(roleId);
        role.setNick(nick);
        role.setPortrait(0);
        role.setRoleLv(1);
        role.setExp(0);
        role.setVip(0);
        role.setRecharge(0);
        role.setGold(0);
        role.setGoldCost(0);
        role.setGoldGive(0);
        role.setRoleState(RoleState.UNCREATED.getState());
        role.setOlTime(0);
        role.setCreateTime(new Date());
        accountDao.insertRole(role);

        account.setRoleId(roleId);
        accountDao.updateAccount(account);

        return role;
    }

    public void updateAccount(Account account) {
        accountDao.updateAccount(account);
    }

    public void updateRole(Role role) {
        accountDao.updateRole(role);
    }

    public void updateAccountOnLogin(Account account, long roleId, int serverId) {
        account.setRoleId(roleId);
        account.setServerId(serverId);
        account.setLoginTime(new Date());
        accountDao.updateAccount(account);
    }

    public void loadAllPlayer() {
        List<Account> accounts = accountDao.selectAllAccount();
        if (CollectionUtil.isNotEmpty(accounts)) {
            Role role;
            TankPlayer player;
            Map<Long, Role> roleMap = loadAllRole();
            for (Account account : accounts) {
                player = new TankPlayer();
                player.setAccount(account);

                if (account.getRoleId() > 0) {
                    role = roleMap.get(account.getRoleId());
                    if (role != null) {
                        player.setRole(role);
                        roleIdToPlayer.put(role.getRoleId(), player);
                        nickToPlayer.put(role.getNick(), player);
                    }
                }

                deviceNoToAccount.put(account.getDeviceNo(), account);
                accountIdToPlayer.put(account.getAccountId(), player);
            }
        }
    }

    private Map<Long, Role> loadAllRole() {
        List<Role> roles = accountDao.selectAllRole();
        Map<Long, Role> roleMap = new HashMap<>(roles.size());
        if (CollectionUtil.isNotEmpty(roles)) {
            for (Role role : roles) {
                roleMap.put(role.getRoleId(), role);
            }
        }
        return roleMap;
    }

    public void playerMoveTimerLogic() {
        for (TankPlayer player : onlinePlayer.values()) {
            player.updatePos();
        }
    }
}
