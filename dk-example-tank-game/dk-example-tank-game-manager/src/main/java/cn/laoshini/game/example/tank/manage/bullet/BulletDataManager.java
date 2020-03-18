package cn.laoshini.game.example.tank.manage.bullet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;

import cn.laoshini.dk.util.CollectionUtil;
import cn.laoshini.dk.util.LogUtil;
import cn.laoshini.game.example.tank.common.TankException;
import cn.laoshini.game.example.tank.constant.TankConstants;
import cn.laoshini.game.example.tank.domain.BulletMove;
import cn.laoshini.game.example.tank.domain.Position;
import cn.laoshini.game.example.tank.domain.TankPlayer;
import cn.laoshini.game.example.tank.entity.StaticBullet;
import cn.laoshini.game.example.tank.entity.StaticTank;
import cn.laoshini.game.example.tank.manage.ini.StaticDataManager;
import cn.laoshini.game.example.tank.manage.player.PlayerDataManager;
import cn.laoshini.game.example.tank.manage.robot.TankRobot;
import cn.laoshini.game.example.tank.manage.room.Room;
import cn.laoshini.game.example.tank.manage.room.RoomDataManager;
import cn.laoshini.game.example.tank.manage.room.RoomRank;
import cn.laoshini.game.example.tank.manage.util.TimerTaskUtil;
import cn.laoshini.game.example.tank.message.room.BulletBlastPush;
import cn.laoshini.game.example.tank.message.room.PlayerDeadPush;
import cn.laoshini.game.example.tank.message.room.TankDisappearPush;
import cn.laoshini.game.example.tank.message.room.TankScoreChangePush;
import cn.laoshini.game.example.tank.util.PositionUtil;

import static cn.laoshini.game.example.tank.constant.GameError.BULLET_NO_CONFIG;

/**
 * @author fagarine
 */
@Component
public class BulletDataManager {

    @Resource
    private StaticDataManager staticDataManager;

    @Resource
    private PlayerDataManager playerDataManager;

    @Resource
    private RoomDataManager roomDataManager;

    private final Map<Integer, BulletMove> bidToBullets = new ConcurrentHashMap<>();

    public int addBulletMove(int roomId, long roleId, int bulletId, Position curPos, Position targetPos) {
        StaticBullet bullet = staticDataManager.getBulletById(bulletId);
        if (null == bullet) {
            throw new TankException(BULLET_NO_CONFIG, "找不到子弹的配置信息:" + bulletId);
        }

        int aliveTime = (bullet.getDistance() / bullet.getSpeed() * 1000);

        BulletMove bm = new BulletMove(roomId, roleId, bulletId, curPos, targetPos, aliveTime);
        bidToBullets.put(bm.getId(), bm);
        return bm.getId();
    }

    public BulletMove getBulletMoveById(int bid) {
        BulletMove bulletMove = bidToBullets.get(bid);
        if (bulletMove != null) {
            // 更新子弹坐标
            updateBulletCurrentPos(bulletMove);
        }
        return bulletMove;
    }

    public void bulletMoveTimerLogic() {
        if (bidToBullets.isEmpty()) {
            return;
        }

        BulletMove bm;
        StaticBullet bullet;
        Set<TankPlayer> roleSet;
        Iterator<BulletMove> its = bidToBullets.values().iterator();
        while (its.hasNext()) {
            bm = its.next();
            if (bm.isBlast()) {
                its.remove();
                continue;
            }
            try {
                bullet = staticDataManager.getBulletById(bm.getBulletId());
                if (null == bullet) {
                    LogUtil.error("找不到子弹的配置信息:" + bm.getBulletId());
                    its.remove();
                    continue;
                }

                roleSet = getRoomPlayers(bm.getRoomId());
                if (CollectionUtil.isEmpty(roleSet)) {
                    its.remove();// 当前没有人，直接删除
                    continue;
                }

                // 更新子弹坐标
                updateBulletCurrentPos(bm);

                // 计算子弹碰撞
                TankPlayer collidePlayer = collideLogic(bm, roleSet);

                if (!bm.isBlast()) {
                    // 子弹已到终点
                    if (bm.isAtTerminus()) {
                        if (bulletNotBlast(bullet.getBlastRadius())) {
                            // 既无爆炸效果，又没有检测到碰撞，直接删除
                            its.remove();
                            continue;
                        } else {
                            // 有爆炸效果的，设置子弹已爆炸
                            bm.setBlast(true);
                        }
                    } else {
                        // 没有检测到碰撞，又未到终点，跳过本次逻辑
                        continue;
                    }
                }

                // 通知客户端子弹爆炸
                Room room = roomDataManager.getRoomById(bm.getRoomId());
                bulletBlast(bm, room);

                Set<TankPlayer> affectedPlayers = new LinkedHashSet<>();
                if (collidePlayer != null) {
                    affectedPlayers.add(collidePlayer);
                }
                // 计算子弹爆炸影响
                affectedPlayers.addAll(blastLogic(bm, bullet.getBlastRadius(), roleSet, collidePlayer));

                // 玩家击中处理
                killLogic(bm, affectedPlayers);

                its.remove();
            } catch (Exception e) {
                LogUtil.error("子弹处理逻辑出错, bulletMove:" + bm, e);
                its.remove();
                throw e;
            }
        }
    }

    private void killLogic(BulletMove bm, Set<TankPlayer> deadPlayers) {
        if (CollectionUtil.isNotEmpty(deadPlayers)) {
            Room room = roomDataManager.getRoomById(bm.getRoomId());
            List<Long> roleIds = new ArrayList<>(deadPlayers.size());
            for (TankPlayer deadPlayer : deadPlayers) {
                roleIds.add(deadPlayer.getRoleId());
            }

            // 推送玩家消失消息
            TankDisappearPush push = TankDisappearPush.builder().roleIds(roleIds).build();
            roomDataManager.pushRoomMessage(room, TankDisappearPush.MESSAGE_ID, push, roleIds);

            int addScore = calculateScore(roleIds.size());
            room.refreshRankOnKill(bm.getRoleId(), addScore, roleIds);
            // 推送排行榜变动消息
            roomDataManager.pushRankListChange(room);

            // 推送攻击者积分变动消息
            RoomRank rank = room.getRoleRank(bm.getRoleId());
            TankPlayer killer = playerDataManager.getPlayerByRoleId(bm.getRoleId());
            if (killer != null) {
                killer.sendRespMessage(TankScoreChangePush.MESSAGE_ID, buildScoreChangePush(rank));
            }

            // 推送玩家自己的积分变动消息
            for (TankPlayer deadPlayer : deadPlayers) {
                deadPlayer.setAlive(false);
                int waitSec = calculateResurgenceDelay(deadPlayer);

                if (!deadPlayer.isRobot()) {
                    rank = room.getRoleRank(deadPlayer.getRoleId());
                    deadPlayer.sendRespMessage(TankScoreChangePush.MESSAGE_ID, buildScoreChangePush(rank));

                    // 给玩家推送自己死亡消息
                    deadPlayer.sendRespMessage(PlayerDeadPush.MESSAGE_ID,
                            PlayerDeadPush.builder().deadNum(rank.getDeadNum()).waitSecond(waitSec).build());
                } else if (deadPlayer instanceof TankRobot) {
                    ((TankRobot) deadPlayer).dead(killer);
                }

                // 开启玩家重生定时任务
                TimerTaskUtil.addTankResurgenceTask(deadPlayer, room, waitSec);
            }
        }
    }

    private TankScoreChangePush buildScoreChangePush(RoomRank rank) {
        return TankScoreChangePush.builder().rank(rank.getRank()).score(rank.getScore()).killNum(rank.getKillNum())
                .deadNum(rank.getDeadNum()).build();
    }

    /**
     * 计算玩家复活时间
     *
     * @param player 玩家对象
     * @return 返回复活等待时间
     */
    private int calculateResurgenceDelay(TankPlayer player) {
        return TankConstants.INIT_RESURGENCE_DELAY;
    }

    private Set<TankPlayer> blastLogic(BulletMove bm, int radius, Set<TankPlayer> roleSet, TankPlayer collidePlayer) {
        // 子弹无范围爆炸效果
        if (bulletNotBlast(radius)) {
            return Collections.emptySet();
        }

        StaticTank tank;
        Position pos = bm.getCurPos();
        Set<TankPlayer> affectedPlayers = new HashSet<>();
        for (TankPlayer player : roleSet) {
            if (!player.isAlive() || player.equals(collidePlayer)) {
                continue;
            }

            tank = staticDataManager.getTankById(player.getTankId());
            if (null == tank) {
                LogUtil.error("未找到坦克配置信息, tankId:" + player.getTankId());
                continue;
            }

            if (PositionUtil.isInCircle(pos, radius, player.getPos(), tank.getRadius())) {
                affectedPlayers.add(player);
                LogUtil.debug("[{}]的子弹爆炸影响到[{}]:[{}]", bm.getRoleId(), player.getNick(), player.getRoleId());
            }
        }

        return affectedPlayers;
    }

    /**
     * 判断子弹是否不能自动爆炸
     *
     * @param radius 子弹影响半径
     * @return 返回判断结果
     */
    private boolean bulletNotBlast(int radius) {
        return radius <= 0;
    }

    /**
     * 子弹爆炸处理
     *
     * @param bm 子弹轨迹
     * @param room 房间
     */
    private void bulletBlast(BulletMove bm, Room room) {
        // 推送子弹爆炸消息
        BulletBlastPush blastPush = BulletBlastPush.builder().bid(bm.getId()).pos(bm.getCurPos()).build();
        roomDataManager.pushToAll(room, BulletBlastPush.MESSAGE_ID, blastPush);
    }

    private TankPlayer collideLogic(BulletMove bm, Set<TankPlayer> roleSet) {
        StaticTank tank;
        Position pos = bm.getCurPos();
        for (TankPlayer player : roleSet) {
            if (!player.isAlive() || player.getRoleId() == bm.getRoleId()) {
                continue;
            }
            tank = staticDataManager.getTankById(player.getTankId());
            if (null == tank) {
                LogUtil.error("未找到坦克配置信息, tankId:" + player.getTankId());
                continue;
            }

            if (PositionUtil
                    .isInTank(player.getPos(), player.getDirection(), tank.getHalfWidth(), tank.getHalfHeight(), pos)) {
                LogUtil.debug("[{}]的子弹命中[{}]:[{}]", bm.getRoleId(), player.getNick(), player.getRoleId());
                // 设置子弹已爆炸
                bm.setBlast(true);
                return player;
            }
        }

        return null;
    }

    public void collideDetectHandle(TankPlayer player, BulletMove bm, int halfTankHeight) {
        if (bm.isBlast()) {
            return;
        }
        boolean isCollide = PositionUtil
                .isInRectByDirection(player.getPos(), player.getDirection(), player.getSpeed(), halfTankHeight << 1,
                        bm.getCurPos());
        if (isCollide) {
            if (bm.isBlast()) {
                return;
            }
            LogUtil.debug("[{}]的子弹命中[{}]:[{}]", bm.getRoleId(), player.getNick(), player.getRoleId());
            // 设置子弹已爆炸
            bm.setBlast(true);

            killLogic(bm, Sets.newHashSet(player));
        }
    }

    public int calculateScore(int kill) {
        return TankConstants.KILL_SCORE * kill;
    }

    /**
     * 根据移动时间更新子弹当前坐标
     *
     * @param bm 子弹轨迹
     */
    private void updateBulletCurrentPos(BulletMove bm) {
        long now = System.currentTimeMillis();
        int time = (int) (now - bm.getCreateTime());
        float rate = time / (bm.getAliveTime() * 1.0f);
        int xOffset = (int) ((bm.getTargetPos().getX() - bm.getStartPos().getX()) * rate);
        int yOffset = (int) ((bm.getTargetPos().getY() - bm.getStartPos().getY()) * rate);

        bm.getCurPos().setX(bm.getStartPos().getX() + xOffset);
        bm.getCurPos().setY(bm.getStartPos().getY() + yOffset);
        LogUtil.debug("更新子弹坐标, curPos:" + bm.getCurPos());
    }

    private Set<TankPlayer> getRoomPlayers(int roomId) {
        Room room = roomDataManager.getRoomById(roomId);
        if (null == room || room.isEmpty()) {
            return null;
        }
        return new HashSet<>(room.getPlayers());
    }

    /**
     * 验证子弹对象是否有效
     *
     * @param bm 子弹移动轨迹
     * @return 返回子弹是否还在移动中
     */
    private boolean bulletIsValid(BulletMove bm) {
        if (null == bm || bm.isBlast()) {
            return false;
        }

        long now = System.currentTimeMillis();
        if (now - bm.getCreateTime() > bm.getAliveTime()) {
            return false;
        }

        return isBetweenTowPos(bm.getCurPos(), bm.getStartPos(), bm.getTargetPos());
    }

    private boolean isBetweenTowPos(Position curPos, Position startPos, Position targetPos) {
        if (null == curPos || null == startPos || null == targetPos) {
            return false;
        }

        return betweenTowPoint(startPos.getX(), targetPos.getX(), curPos.getX()) && betweenTowPoint(startPos.getY(),
                targetPos.getY(), curPos.getY());
    }

    private boolean betweenTowPoint(int p1, int p2, int point) {
        if (p1 < p2) {
            return p1 <= point && point <= p2;
        } else {
            return p1 >= point && point >= p2;
        }
    }
}
