package cn.laoshini.game.example.tank.manage.room;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cn.laoshini.dk.domain.msg.RespMessage;
import cn.laoshini.dk.util.LogUtil;
import cn.laoshini.game.example.tank.constant.RoomType;
import cn.laoshini.game.example.tank.constant.TankConstants;
import cn.laoshini.game.example.tank.domain.TankPlayer;
import cn.laoshini.game.example.tank.dto.RoomRankDTO;
import cn.laoshini.game.example.tank.manage.player.PlayerDataManager;
import cn.laoshini.game.example.tank.message.room.RoomRankChangePush;
import cn.laoshini.game.example.tank.util.TimeHelper;

/**
 * @author fagarine
 */
@Component
public class RoomDataManager {

    @Resource
    private PlayerDataManager playerDataManager;

    private Map<Integer, Room> allRoom = new ConcurrentHashMap<>();

    private Queue<Room> invalidRooms = new ConcurrentLinkedQueue<>();

    private static int roomId = TimeHelper.getCurrentSecond() / 10000;

    public Room newRoom(RoomType roomType, long creatorId, String creator) {
        Room room = new Room(nextRoomId(), roomType, creatorId, creator);
        allRoom.put(room.getRoomId(), room);
        return room;
    }

    public Room newRoom(long creatorId, String creator) {
        return newRoom(RoomType.NORMAL, creatorId, creator);
    }

    public Room newSecretRoom(long creatorId, String creator) {
        return newRoom(RoomType.SECRET, creatorId, creator);
    }

    public Room getValidRoom(long roleId, String nick) {
        Room room = getNotFullRoom();
        if (null == room) {
            room = newRoom(roleId, nick);
        }

        return room;
    }

    public Room getNotFullRoom() {
        for (Room room : allRoom.values()) {
            if (!room.isFull()) {
                return room;
            }
        }
        return null;
    }

    private static int nextRoomId() {
        return roomId++;
    }

    public Room getRoomById(int roomId) {
        return allRoom.get(roomId);
    }

    public void roleExitRoom(int roomId, TankPlayer player) {
        Room room = getRoomById(roomId);
        if (room != null) {
            room.roleExitRoom(player);
        }
    }

    public List<Room> getAllRoom() {
        return new ArrayList<>(allRoom.values());
    }

    private void recordRoomInvalid(int roomId) {
        Room room = allRoom.remove(roomId);
        if (room != null) {
            invalidRooms.offer(room);
        }
    }

    public void roomTimerLogic() {
        try {
            if (!invalidRooms.isEmpty()) {
                while (!invalidRooms.isEmpty()) {
                    Room room = invalidRooms.poll();
                    LogUtil.debug("房间[{}]被移除", room.getRoomName());
                    room.clear();
                }
            }

            if (!allRoom.isEmpty()) {
                long now = System.currentTimeMillis();
                List<Room> rooms = new ArrayList<>(allRoom.values());
                for (Room room : rooms) {
                    if (room.isEmpty()) {
                        if (room.getRemoveTime() > 0) {
                            if (room.getRemoveTime() <= now) {
                                LogUtil.debug("房间[{}]已失效，加入移除队列", room.getRoomName());
                                recordRoomInvalid(room.getRoomId());
                            }
                        } else {
                            room.setRemoveTime(now + TankConstants.ROOM_INVALID_TIME);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.error("房间定时任务执行出错", e);
        }
    }

    public void pushRoomMessage(Room room, RespMessage<?> push, long excludeRoleId) {
        for (TankPlayer player : room.getPlayers()) {
            if (excludeRoleId != player.getRoleId() && !player.isRobot()) {
                player.sendRespMessage(push);
            }
        }
    }

    public void pushRoomMessage(Room room, int messageId, Object push, long excludeRoleId) {
        RespMessage<Object> resp = new RespMessage<>();
        resp.setId(messageId);
        resp.setData(push);
        pushRoomMessage(room, resp, excludeRoleId);
    }

    public void pushToAll(Room room, int messageId, Object push) {
        pushRoomMessage(room, messageId, push, 0);
    }

    public void pushRoomMessage(Room room, RespMessage<?> push, Collection<Long> excludeRoleIds) {
        for (TankPlayer player : room.getPlayers()) {
            if (excludeRoleIds == null || !excludeRoleIds.contains(player.getRoleId())) {
                player.sendRespMessage(push);
            }
        }
    }

    public void pushRoomMessage(Room room, int messageId, Object push, Collection<Long> excludeRoleIds) {
        RespMessage<Object> resp = new RespMessage<>();
        resp.setId(messageId);
        resp.setData(push);
        pushRoomMessage(room, resp, excludeRoleIds);
    }

    public void pushRankListChange(Room room) {
        List<RoomRankDTO> ranks = new LinkedList<>();
        for (RoomRank rank : room.getRankList()) {
            if (rank.getRank() >= TankConstants.ROOM_RANK_SIZE) {
                break;
            }
            ranks.add(buildRoomRank(rank));
        }
        RoomRankChangePush rankChangePush = RoomRankChangePush.builder().ranks(ranks).build();
        pushToAll(room, RoomRankChangePush.MESSAGE_ID, rankChangePush);
    }

    public RoomRankDTO buildRoomRank(RoomRank rank) {
        return RoomRankDTO.builder().roleId(rank.getRoleId()).nick(rank.getNick()).rank(rank.getRank())
                .score(rank.getScore()).build();
    }
}
