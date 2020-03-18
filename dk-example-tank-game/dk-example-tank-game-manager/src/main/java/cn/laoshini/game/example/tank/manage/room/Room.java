package cn.laoshini.game.example.tank.manage.room;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import cn.laoshini.dk.util.CollectionUtil;
import cn.laoshini.game.example.tank.constant.RoomType;
import cn.laoshini.game.example.tank.constant.TankConstants;
import cn.laoshini.game.example.tank.domain.TankPlayer;
import cn.laoshini.game.example.tank.manage.robot.TankRobot;
import cn.laoshini.game.example.tank.util.TimeHelper;

/**
 * @author fagarine
 */
@Getter
@Setter
@ToString
public class Room {

    /**
     * 房间号
     */
    private int roomId;

    /**
     * 房间名称
     */
    private String roomName;

    /**
     * 房间类型，详见{@link RoomType}
     */
    private RoomType roomType;

    /**
     * 房间最大容纳玩家数量
     */
    private int maxPlayer;

    /**
     * 创建者id
     */
    private long creatorId;

    /**
     * 记录当前在房间内的玩家
     */
    private Set<TankPlayer> players = Collections.synchronizedSet(new HashSet<>());

    /**
     * 记录排行榜数据
     */
    private final List<RoomRank> rankList = new LinkedList<>();

    /**
     * 记录房间中的机器人信息
     */
    private List<TankRobot> robots = new LinkedList<>();

    /**
     * 记录房间移除时间
     */
    private long removeTime;

    public Room() {
    }

    public Room(int roomId, RoomType roomType, long creatorId, String creator) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.maxPlayer = roomType.getCapacity();
        this.roomName = creator + "的房间";
        this.creatorId = creatorId;

        addRoomRank(creatorId, creator);
    }

    public boolean isFull() {
        return players.size() >= maxPlayer;
    }

    public boolean isEmpty() {
        return players.isEmpty() || getRoleNum() == getRobotNum();
    }

    private void updateRankIndex() {
        synchronized (rankList) {
            rankList.sort(RoomRank::compareTo);
            int index = 0;
            for (RoomRank roomRank : rankList) {
                roomRank.setRank(index++);
            }
        }
    }

    public RoomRank getRoleRank(long roleId) {
        for (RoomRank roomRank : rankList) {
            if (roomRank.getRoleId() == roleId) {
                return roomRank;
            }
        }
        return null;
    }

    public RoomRank addRoomRank(long roleId, String nick) {
        RoomRank rank = getRoleRank(roleId);
        if (rank == null) {
            rank = new RoomRank();
            rank.setNick(nick);
            rank.setRoleId(roleId);
            rank.setKillNum(0);
            rank.setDeadNum(0);
            rank.setScore(0);
            rank.setRankTime(TimeHelper.getCurrentSecond());
            rankList.add(rank);
            updateRankIndex();
        }
        return rank;
    }

    public boolean updateRoomRank(long roleId, int addKill, int addDead, int addScore) {
        RoomRank rank = getRoleRank(roleId);
        rank.addKill(addKill);
        rank.addDead(addDead);
        rank.addScore(addScore);
        rank.setRankTime(TimeHelper.getCurrentSecond());

        int originRank = rank.getRank();
        updateRankIndex();
        int currentRank = rank.getRank();

        return isShowRankListChanged(originRank, currentRank);
    }

    public void refreshRankOnKill(long killerId, int addScore, Collection<Long> deadIds) {
        if (CollectionUtil.isEmpty(deadIds)) {
            return;
        }

        int addKill = deadIds.size();
        updateRoomRank(killerId, addKill, 0, addScore);
        for (long deadId : deadIds) {
            updateRoomRank(deadId, 0, 1, 0);
        }
    }

    public boolean isShowRankListChanged(int originRank, int currentRank) {
        if (isInShowRank(originRank)) {
            return !isInShowRank(currentRank);
        } else {
            return isInShowRank(currentRank);
        }
    }

    public int getRoleRankIndex(long roleId) {
        for (RoomRank roomRank : getRankList()) {
            if (roomRank.getRoleId() == roleId) {
                return roomRank.getRank();
            }
        }
        return -1;
    }

    public boolean roleIsInShowRank(long roleId) {
        int rank = getRoleRankIndex(roleId);
        return isInShowRank(rank);
    }

    public boolean isInShowRank(int rankIndex) {
        return rankIndex >= 0 && rankIndex < TankConstants.ROOM_RANK_SIZE;
    }

    public RoomRank roleEnterRoom(TankPlayer player, String nick) {
        players.add(player);

        if (player instanceof TankRobot) {
            robots.add((TankRobot) player);
        } else {
            removeTime = 0;
        }
        return addRoomRank(player.getRoleId(), nick);
    }

    public void roleExitRoom(TankPlayer player) {
        players.remove(player);
        rankList.removeIf(rank -> rank.getRoleId() == player.getRoleId());

        if (player instanceof TankRobot) {
            robots.remove(player);
        }
    }

    public void clear() {
        roomId = 0;
        roomName = null;
        roomType = null;
        players.clear();
        players = null;
        robots.clear();
        rankList.clear();
    }

    public TankPlayer getPlayerById(long roleId) {
        for (TankPlayer player : players) {
            if (player.getRoleId() == roleId) {
                return player;
            }
        }
        return null;
    }

    public int getRoleNum() {
        return players.size();
    }

    public int getPlayerNum() {
        return getRoleNum() - getRobotNum();
    }

    public int getRobotNum() {
        return robots.size();
    }

    public boolean containsRobot() {
        return !robots.isEmpty();
    }

    /**
     * 房间是否已经很热闹，即已经有很多人（这里认为达到房间容量的2/3就算人多），用于机器人逻辑
     *
     * @return 返回判断结果
     */
    public boolean isBoisterous() {
        int spare = maxPlayer - getRoleNum();
        return getRoleNum() / spare >= 2;
    }

}
