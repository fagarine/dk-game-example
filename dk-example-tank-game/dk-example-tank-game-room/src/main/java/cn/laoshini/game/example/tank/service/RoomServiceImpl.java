package cn.laoshini.game.example.tank.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import cn.laoshini.dk.domain.msg.RespMessage;
import cn.laoshini.dk.util.LogUtil;
import cn.laoshini.game.example.tank.common.TankException;
import cn.laoshini.game.example.tank.constant.GameError;
import cn.laoshini.game.example.tank.constant.TankConstants;
import cn.laoshini.game.example.tank.domain.BulletMove;
import cn.laoshini.game.example.tank.domain.Position;
import cn.laoshini.game.example.tank.domain.RoomInfo;
import cn.laoshini.game.example.tank.domain.RoomRole;
import cn.laoshini.game.example.tank.domain.TankPlayer;
import cn.laoshini.game.example.tank.dto.RoomRankDTO;
import cn.laoshini.game.example.tank.entity.Role;
import cn.laoshini.game.example.tank.entity.StaticBullet;
import cn.laoshini.game.example.tank.manage.bullet.BulletDataManager;
import cn.laoshini.game.example.tank.manage.ini.StaticDataManager;
import cn.laoshini.game.example.tank.manage.room.Room;
import cn.laoshini.game.example.tank.manage.room.RoomDataManager;
import cn.laoshini.game.example.tank.manage.room.RoomRank;
import cn.laoshini.game.example.tank.message.room.EnterRoomPush;
import cn.laoshini.game.example.tank.message.room.EnterRoomReq;
import cn.laoshini.game.example.tank.message.room.EnterRoomRes;
import cn.laoshini.game.example.tank.message.room.ExitRoomReq;
import cn.laoshini.game.example.tank.message.room.ExitRoomRes;
import cn.laoshini.game.example.tank.message.room.GetRoomRankReq;
import cn.laoshini.game.example.tank.message.room.GetRoomRankRes;
import cn.laoshini.game.example.tank.message.room.TankCollideReq;
import cn.laoshini.game.example.tank.message.room.TankDisappearPush;
import cn.laoshini.game.example.tank.message.room.TankMovePush;
import cn.laoshini.game.example.tank.message.room.TankMoveReq;
import cn.laoshini.game.example.tank.message.room.TankMoveRes;
import cn.laoshini.game.example.tank.message.room.TankShotPush;
import cn.laoshini.game.example.tank.message.room.TankShotReq;
import cn.laoshini.game.example.tank.message.room.TankShotRes;
import cn.laoshini.game.example.tank.util.PositionUtil;

/**
 * @author fagarine
 */
@Service
public class RoomServiceImpl implements IRoomService {

    @Resource
    private RoomDataManager roomDataManager;

    @Resource
    private StaticDataManager staticDataManager;

    @Resource
    private BulletDataManager bulletDataManager;

    @Override
    public List<RoomInfo> getRooms() throws TankException {
        List<RoomInfo> rooms = new LinkedList<>();
        List<Room> allRoom = roomDataManager.getAllRoom();
        int num = 1;
        for (Room room : allRoom) {
            if (num++ > 100) {
                break;
            }
            rooms.add(RoomInfo.builder().roomId(room.getRoomId()).roomName(room.getRoomName())
                    .maxNum(room.getRoomType().getCapacity()).secret(room.getRoomType().isSecret())
                    .roleNum(room.getRoleNum()).build());
        }
        return rooms;
    }

    @Override
    public void enterRoom(EnterRoomReq req, TankPlayer player) throws TankException {
        Role role = player.getRole();
        long roleId = player.getRoleId();
        if (player.getRoomId() > 0) {
            if (roomDataManager.getRoomById(player.getRoomId()) != null) {
                throw new TankException(GameError.ALREADY_IN_ROOM,
                        "玩家已在房间中, roleId:" + roleId + ", nick:" + player.getNick());
            }
        }

        Room room;
        boolean create = req.getCreate() != null && req.getCreate();
        Integer roomId = req.getRoomId();
        if (roomId != null && roomId > 0) {
            room = roomDataManager.getRoomById(roomId);
            if (room == null) {
                throw new TankException(GameError.ROOM_NOT_FOUND,
                        "未找到房间, roleId:" + roleId + ", nick:" + player.getNick() + ", roomId:" + roomId);
            }
        } else {
            if (create) {
                room = roomDataManager.newRoom(roleId, role.getNick());
            } else {
                room = roomDataManager.getValidRoom(roleId, role.getNick());
            }
        }

        // 玩家进入房间
        room.roleEnterRoom(player, role.getNick());
        player.setRoomId(room.getRoomId());
        player.setTankId(TankConstants.INIT_TANK_ID);
        // 玩家排行信息
        RoomRank rank = room.getRoleRank(roleId);

        // 随机玩家坐标
        Position pos = PositionUtil
                .randomPosition(TankConstants.ROOM_X_LEN, TankConstants.ROOM_Y_LEN, TankConstants.ROOM_BORDER);
        player.setPos(pos);
        player.setDirection(0);
        player.setAlive(true);
        player.setSpeed(TankConstants.INIT_SPEED);

        // 返回消息
        List<RoomRole> roles = new LinkedList<>();
        List<TankPlayer> players = new ArrayList<>(room.getRoleNum());
        for (TankPlayer roomPlayer : room.getPlayers()) {
            if (roomPlayer.getRoleId() == roleId) {
                continue;
            }
            roles.add(buildRoomRole(roomPlayer.getRoleId(), roomPlayer.getNick(), roomPlayer.getPortrait(),
                    roomPlayer.getSpeed(), roomPlayer.getDirection(), roomPlayer.getPos()));
            players.add(roomPlayer);
        }
        EnterRoomRes res = EnterRoomRes.builder().roomId(room.getRoomId()).roomName(room.getRoomName()).roleId(roleId)
                .nick(role.getNick()).portrait(role.getPortrait()).rank(rank.getRank()).score(rank.getScore())
                .deadNum(rank.getDeadNum()).killNum(rank.getKillNum()).direction(player.getDirection())
                .speed(TankConstants.INIT_SPEED).pos(player.getPos()).roles(roles).build();
        player.sendRespMessage(EnterRoomRes.MESSAGE_ID, res);

        // 向其他人推送新玩家进入房间消息
        RespMessage<EnterRoomPush> push = new RespMessage<>();
        push.setId(EnterRoomPush.MESSAGE_ID);
        push.setData(EnterRoomPush.builder()
                .role(buildRoomRole(roleId, role.getNick(), role.getPortrait(), player.getSpeed(),
                        player.getDirection(), player.getPos())).build());
        for (TankPlayer tankPlayer : players) {
            tankPlayer.sendRespMessage(push);
        }

        if (!create && room.getPlayerNum() > 1 && rank.getRank() < TankConstants.ROOM_RANK_SIZE) {
            // 排行榜变更消息
            roomDataManager.pushRankListChange(room);
        }
    }

    @Override
    public void getRoomRank(GetRoomRankReq req, TankPlayer player) throws TankException {
        long roleId = player.getRoleId();
        int roomId = player.getRoomId();
        if (roomId <= 0) {
            throw new TankException(GameError.NOT_IN_ROOM,
                    "玩家没有在房间内，不能获取排行榜信息, roleId:" + roleId + ", nick:" + player.getNick() + ", roomId:" + roomId);
        }

        Room room = roomDataManager.getRoomById(roomId);
        if (null == room) {
            throw new TankException(GameError.ROOM_NOT_EXISTS,
                    "房间不存在，不能获取排行榜信息, roleId:" + roleId + ", nick:" + player.getNick() + ", roomId:" + roomId);
        }

        // 返回消息
        int myRank = -1;
        List<RoomRankDTO> ranks = new LinkedList<>();
        for (RoomRank rank : room.getRankList()) {
            if (rank.getRoleId() == roleId) {
                myRank = rank.getRank();
            }

            if (rank.getRank() < TankConstants.ROOM_RANK_SIZE) {
                ranks.add(buildRoomRank(rank));
            } else if (myRank >= 0) {
                break;
            }
        }
        player.sendRespMessage(GetRoomRankRes.MESSAGE_ID, GetRoomRankRes.builder().myRank(myRank).ranks(ranks).build());
    }

    @Override
    public void tankMove(TankMoveReq req, TankPlayer player) throws TankException {
        int direction = req.getDirection();
        long now = System.currentTimeMillis();
        if (now < player.getNextMoveTime() && direction == player.getDirection() && !req.getPos()
                .equals(req.getTarget())) {
            // 同一方向移动消息太频繁，直接吞掉
            return;
        }

        long roleId = player.getRoleId();
        int roomId = player.getRoomId();
        if (roomId <= 0) {
            throw new TankException(GameError.NOT_IN_ROOM,
                    "玩家没有在房间内，不能移动, roleId:" + roleId + ", nick:" + player.getNick() + ", roomId:" + roomId);
        }

        Room room = roomDataManager.getRoomById(roomId);
        if (null == room) {
            throw new TankException(GameError.ROOM_NOT_EXISTS,
                    "房间不存在，不能移动, roleId:" + roleId + ", nick:" + player.getNick() + ", roomId:" + roomId);
        }

        if (!player.isAlive()) {
            throw new TankException(GameError.PLAYER_DEAD,
                    "玩家已死亡，不能移动, roleId:" + roleId + ", nick:" + player.getNick() + ", roomId:" + roomId);
        }

        // 坐标检查
        checkPlayerPos(player, req.getPos());

        // 设置玩家的最后坐标
        player.getPos().toPosition(req.getPos());
        player.beginMove(req.getTarget());
        player.setNextMoveTime(now + 500);

        // 返回消息
        TankMoveRes res = TankMoveRes.builder().pos(req.getPos()).target(req.getTarget()).direction(direction).build();
        player.sendRespMessage(TankMoveRes.MESSAGE_ID, res);

        // 推送玩家移动消息
        RespMessage<TankMovePush> push = new RespMessage<>();
        push.setId(TankMovePush.MESSAGE_ID);
        push.setData(TankMovePush.builder().roleId(roleId).pos(req.getPos()).target(req.getTarget())
                .direction(req.getDirection()).build());
        pushRoomMessage(room, push, roleId);
    }

    private void checkPlayerPos(TankPlayer player, Position pos) {
        if (!PositionUtil.isInCircle(player.getPos(), player.getSpeed(), pos)) {
            throw new TankException(GameError.INVALID_POS,
                    "传入非法坐标, roleId:" + player.getRoleId() + ", nick:" + player.getNick() + ", roomId:" + player
                            .getRoomId() + ", server pos:" + player.getPos() + ", client pos:" + pos);
        }
    }

    @Override
    public void tankShot(TankShotReq req, TankPlayer player) throws TankException {
        long roleId = player.getRoleId();
        int roomId = player.getRoomId();
        if (roomId <= 0) {
            throw new TankException(GameError.NOT_IN_ROOM,
                    "玩家没有在房间内，不能射击, roleId:" + roleId + ", nick:" + player.getNick() + ", roomId:" + roomId);
        }

        Room room = roomDataManager.getRoomById(roomId);
        if (null == room) {
            throw new TankException(GameError.ROOM_NOT_EXISTS,
                    "房间不存在，不能射击, roleId:" + roleId + ", nick:" + player.getNick() + ", roomId:" + roomId);
        }

        if (!player.isAlive()) {
            throw new TankException(GameError.PLAYER_DEAD,
                    "玩家已死亡，不能射击, roleId:" + roleId + ", nick:" + player.getNick() + ", roomId:" + roomId);
        }

        // 坐标检查
        checkPlayerPos(player, req.getPos());

        int bulletId = req.getBulletId();
        StaticBullet bullet = staticDataManager.getBulletById(bulletId);
        if (null == bullet) {
            throw new TankException(GameError.BULLET_NO_CONFIG,
                    "玩家射击时，子弹id无此配置, roleId:" + roleId + ", nick:" + player.getNick() + ", roomId:" + roomId);
        }

        // 检查CD
        long now = System.currentTimeMillis();
        if (now < player.getNextShotTime()) {
            throw new TankException(GameError.SHOT_CD,
                    "玩家射击CD, roleId:" + roleId + ", nick:" + player.getNick() + ", roomId:" + roomId);
        }

        int direction = req.getDirection();
        Position curPos = req.getPos();
        Position targetPos = PositionUtil.toTargetPos(curPos, bullet.getDistance(), direction);

        // 记录子弹轨迹
        int bid = bulletDataManager.addBulletMove(roomId, roleId, bulletId, curPos, targetPos);

        player.setNextShotTime(now + bullet.getCoolTime());
        player.getPos().toPosition(req.getPos());

        // 返回消息
        player.sendRespMessage(TankShotRes.MESSAGE_ID,
                TankShotRes.builder().bid(bid).bulletId(bulletId).direction(direction).speed(bullet.getSpeed())
                        .from(curPos).target(targetPos).build());

        // 推送玩家射击消息
        RespMessage<TankShotPush> push = new RespMessage<>();
        push.setId(TankShotPush.MESSAGE_ID);
        push.setData(TankShotPush.builder().bid(bid).bulletId(bulletId).direction(direction).speed(bullet.getSpeed())
                .from(curPos).target(targetPos).build());
        pushRoomMessage(room, push, roleId);
    }

    @Override
    public void exitRoom(ExitRoomReq req, TankPlayer player) throws TankException {
        long roleId = player.getRoleId();
        int roomId = player.getRoomId();
        if (roomId <= 0) {
            throw new TankException(GameError.NOT_IN_ROOM,
                    "玩家没有在房间内，不能离开, roleId:" + roleId + ", nick:" + player.getNick() + ", roomId:" + roomId);
        }

        Room room = roomDataManager.getRoomById(roomId);
        if (null == room) {
            throw new TankException(GameError.ROOM_NOT_EXISTS,
                    "房间不存在，不能离开, roleId:" + roleId + ", nick:" + player.getNick() + ", roomId:" + roomId);
        }

        int rankIndex = room.getRoleRankIndex(roleId);

        // 重置玩家房间号
        player.setRoomId(0);
        player.setTankId(0);

        room.roleExitRoom(player);

        // 返回消息
        player.sendRespMessage(ExitRoomRes.MESSAGE_ID, ExitRoomRes.builder().build());

        if (!room.isEmpty()) {
            // 推送玩家消失消息
            RespMessage<TankDisappearPush> disappearPush = new RespMessage<>();
            disappearPush.setId(TankDisappearPush.MESSAGE_ID);
            disappearPush.setData(TankDisappearPush.builder().roleIds(Lists.newArrayList(roleId)).build());
            pushRoomMessage(room, disappearPush, roleId);

            if (room.isInShowRank(rankIndex)) {
                // 推送排行榜变更消息
                roomDataManager.pushRankListChange(room);
            }
        }
    }

    @Override
    public void tankCollide(TankCollideReq req, TankPlayer player) throws TankException {
        long roleId = player.getRoleId();
        int roomId = player.getRoomId();
        if (roomId <= 0) {
            LogUtil.error("玩家没有在房间内，却发送了碰撞检测消息, roleId:[{}], nick:[{}], roomId:[{}]", roleId, player.getNick(), roomId);
            return;
        }

        Room room = roomDataManager.getRoomById(roomId);
        if (null == room) {
            LogUtil.error("玩家房间不存在，却发送了碰撞检测消息, roleId:[{}], nick:[{}], roomId:[{}]", roleId, player.getNick(), roomId);
            return;
        }

        TankPlayer target = room.getPlayerById(req.getRoleId());
        if (target == null) {
            LogUtil.error("未在房间内找到发生碰撞的玩家, roleId:[{}], roomId:[{}]", req.getRoleId(), roomId);
            return;
        }

        checkPlayerPos(target, req.getPos());

        BulletMove bm = bulletDataManager.getBulletMoveById(req.getBid());
        if (bm == null) {
            LogUtil.error("未在房间内找到对应的子弹, bid:[{}], roomId:[{}]", req.getBid(), roomId);
            return;
        }

        target.getPos().toPosition(req.getPos());
    }

    private void pushRoomMessage(Room room, RespMessage<?> push, long excludeRoleId) {
        roomDataManager.pushRoomMessage(room, push, excludeRoleId);
    }

    private RoomRankDTO buildRoomRank(RoomRank rank) {
        return roomDataManager.buildRoomRank(rank);
    }

    private RoomRole buildRoomRole(long roleId, String nick, int portrait, int speed, int direction, Position pos) {
        return RoomRole.builder().roleId(roleId).nick(nick).portrait(portrait).speed(speed).direction(direction)
                .pos(pos).build();
    }
}
