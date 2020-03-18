package cn.laoshini.game.example.tank.manage.robot;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.RandomUtils;

import cn.laoshini.dk.common.SpringContextHolder;
import cn.laoshini.dk.robot.fsm.IFsmRobot;
import cn.laoshini.dk.util.LogUtil;
import cn.laoshini.game.example.tank.config.TankRobotConfig;
import cn.laoshini.game.example.tank.constant.TankConstants;
import cn.laoshini.game.example.tank.domain.Position;
import cn.laoshini.game.example.tank.domain.TankPlayer;
import cn.laoshini.game.example.tank.entity.StaticBullet;
import cn.laoshini.game.example.tank.manage.bullet.BulletDataManager;
import cn.laoshini.game.example.tank.manage.ini.StaticDataManager;
import cn.laoshini.game.example.tank.manage.robot.constant.TankRobotState;
import cn.laoshini.game.example.tank.manage.room.Room;
import cn.laoshini.game.example.tank.manage.room.RoomDataManager;
import cn.laoshini.game.example.tank.message.room.TankMovePush;
import cn.laoshini.game.example.tank.message.room.TankShotPush;
import cn.laoshini.game.example.tank.util.PositionUtil;

import static cn.laoshini.game.example.tank.manage.robot.constant.TankRobotState.*;

/**
 * @author fagarine
 */
@Getter
@Setter
public class TankRobot extends TankPlayer implements IFsmRobot<TankRobotState> {

    private static TankRobotConfig robotConfig = SpringContextHolder.getBean(TankRobotConfig.class);

    private static RoomDataManager roomDataManager = SpringContextHolder.getBean(RoomDataManager.class);

    private static StaticDataManager staticDataManager = SpringContextHolder.getBean(StaticDataManager.class);

    private static BulletDataManager bulletDataManager = SpringContextHolder.getBean(BulletDataManager.class);

    private long roleId;

    private String nick;

    private int portrait;

    private Room room;

    private long idleEndTime;

    private TankPlayer enemy;

    private TankRobotState currentState;

    @Override
    public TankRobotState currentState() {
        return currentState;
    }

    @Override
    public void changeState(TankRobotState newState) {
        TankRobotState state = currentState();
        if (state != null) {
            state.exit(this);
        }

        this.currentState = newState;

        this.currentState.enter(this);
    }

    @Override
    public void tick() {
        TankRobotState state = currentState();

        if (state == null) {
            changeState(SLEEP);
            return;
        } else if (SLEEP.equals(currentState())) {
            return;
        }

        state.refresh(this);
    }

    void wakeUp() {
        if (isSleep()) {
            changeState(IDLE);
        }
    }

    public void idleEnter() {
        // 原地停顿若干秒
        setIdleEndTime(System.currentTimeMillis() + RandomUtils.nextInt(500, robotConfig.getRobotIdleMax()));
    }

    public void idleEnd() {
        changeState(PATROL);
    }

    /**
     * 巡逻逻辑
     */
    public void patrol() {
        if (move.isMoveEnd()) {
            direction = RandomUtils.nextInt(0, 8);
            int len = RandomUtils.nextInt(0, TankConstants.INIT_SPEED * 5);
            Position target = PositionUtil
                    .randomPosition(pos, direction, len, TankConstants.ROOM_X_LEN, TankConstants.ROOM_Y_LEN,
                            TankConstants.ROOM_BORDER);
            if (!target.equals(pos)) {
                beginMove(target);
            }
        } else {
            updatePos();
            searchEnemy();
        }
    }

    /**
     * 开始移动
     *
     * @param target 目标坐标
     */
    @Override
    public void beginMove(Position target) {
        super.beginMove(target);
        //        LogUtil.debug("机器人[{}]开始移动, pos:{}, target:{}", nick, pos, target);

        roomDataManager.pushToAll(room, TankMovePush.MESSAGE_ID,
                TankMovePush.builder().roleId(roleId).direction(direction).pos(pos).target(target).build());
    }

    @Override
    public void moveEnd(Position target) {
        if (move.isMoveEnd()) {
            return;
        }
        //        LogUtil.debug("机器人运动结束, pos:[{}], target:[{}]", pos, target);
        move.setMoveEnd(true);
        if (target != null) {
            pos.toPosition(target);
        }
        roomDataManager.pushToAll(room, TankMovePush.MESSAGE_ID,
                TankMovePush.builder().roleId(roleId).direction(direction).pos(pos).target(new Position(pos)).build());
    }

    private void updateDirection(int direction) {
        //        LogUtil.debug("机器人改变朝向, current:[{}], direction:[{}]", this.direction, direction);
        this.direction = direction;
        move.setMoveEnd(true);
        roomDataManager.pushToAll(room, TankMovePush.MESSAGE_ID,
                TankMovePush.builder().roleId(roleId).direction(direction).pos(pos).target(new Position(pos)).build());
    }

    /**
     * 搜寻敌人并改变状态
     */
    private void searchEnemy() {
        TankPlayer player = lookForPlayer();
        if (player != null) {
            moveEnd(null);
            changeState(FOLLOW);
            //            LogUtil.debug("机器人[{}]跟随对象[{}]:[{}]", nick, player.getNick(), player.getRoleId());
            enemy = player;
        }
    }

    /**
     * 寻找敌人
     *
     * @return 返回发现的角色
     */
    private TankPlayer lookForPlayer() {
        TankPlayer enemy = null;
        int minDistance = TankConstants.ROOM_X_LEN;
        for (TankPlayer player : room.getPlayers()) {
            if (player == this) {
                continue;
            }
            int distance = (int) PositionUtil.distance(pos, player.getPos());
            if (distance <= robotConfig.getRobotLookRadius() && distance < minDistance) {
                minDistance = distance;
                enemy = player;
            }
        }
        return enemy;
    }

    /**
     * 跟随状态逻辑
     */
    public void follow() {
        if (enemy == null || !enemy.isAlive() || enemy.getRoomId() != room.getRoomId()) {
            changeToIdle();
            return;
        }

        if (move.isMoveEnd()) {
            followEnemy();
        } else {
            updatePos();
            // 进入攻击距离，转变为攻击状态
            int distance = (int) PositionUtil.distance(pos, enemy.getPos());
            if (distance < robotConfig.getRobotAttackRadius()) {
                //                LogUtil.debug("机器人[{}]与[{}]进入攻击距离", nick, enemy.getNick());
                changeState(ATTACK);
            } else if (distance > robotConfig.getRobotLookRadius()) {
                // 超出可探索半径，目标丢失，重置状态
                changeToIdle();
            } else {
                // 目标已移动超过一秒，更改跟踪坐标
                int enemyMoved = (int) PositionUtil.distance(move.getTarget(), enemy.getPos());
                if (enemyMoved > enemy.getSpeed()) {
                    followEnemy();
                }
            }
        }
    }

    /**
     * 跟随角色移动
     */
    private void followEnemy() {
        direction = PositionUtil.toDirection(pos, enemy.getPos());
        int len = (int) PositionUtil.distance(pos, enemy.getPos());
        beginMove(PositionUtil.toTargetPos(pos, len, direction));
    }

    /**
     * 攻击状态逻辑
     */
    public void attack() {
        if (enemy == null || !enemy.isAlive() || enemy.getRoomId() != room.getRoomId()) {
            enemy = null;
            searchEnemy();
            if (enemy == null) {
                changeToIdle();
            }
            return;
        }

        if (!move.isMoveEnd()) {
            updatePos();
        }

        double distance = PositionUtil.distance(pos, enemy.getPos());
        if (distance > robotConfig.getRobotAttackRadius()) {
            moveEnd(null);
            changeState(FOLLOW);
        } else {
            if (!move.isMoveEnd() && distance < TankConstants.ROOM_BORDER * 3) {
                moveEnd(null);
            }

            int direction = PositionUtil.toDirection(pos, enemy.getPos());
            //            LogUtil.debug("d:[{}], pos:{}, target:{}, direction:[{}]", this.direction, pos, enemy.getPos(), direction);
            if (direction != this.direction) {
                updateDirection(direction);
            }
            long now = System.currentTimeMillis();
            if (nextShotTime <= 0 || now >= nextShotTime) {
                shot(now);
            }
        }
    }

    /**
     * 射击
     *
     * @param now 当前时间
     */
    private void shot(long now) {
        StaticBullet bullet = staticDataManager.getBulletById(TankConstants.INIT_BULLET_ID);
        Position curPos = new Position(pos);
        Position targetPos = PositionUtil.toTargetPos(curPos, bullet.getDistance(), direction);

        // 记录子弹轨迹
        int bid = bulletDataManager.addBulletMove(room.getRoomId(), roleId, bullet.getBulletId(), curPos, targetPos);

        setNextShotTime(now + RandomUtils.nextInt(bullet.getCoolTime(), bullet.getCoolTime() * 3));
        //        LogUtil.debug("机器人[{}]射击, pos:{}, target:{}", nick, curPos, targetPos);

        // 推送玩家射击消息
        roomDataManager.pushToAll(room, TankShotPush.MESSAGE_ID,
                TankShotPush.builder().bid(bid).bulletId(bullet.getBulletId()).direction(direction)
                        .speed(bullet.getSpeed()).from(curPos).target(targetPos).build());
    }

    /**
     * 机器人死亡时触发
     *
     * @param killer 击杀机器人的角色
     */
    public void dead(TankPlayer killer) {
        LogUtil.debug("机器人[{}]死亡，杀手为[{}]", nick, killer);
        enemy = killer;
        changeState(DEAD);
    }

    /**
     * 复活时触发
     */
    public void resurgence() {
        if (enemy != null && enemy.isAlive()) {
            LogUtil.debug("机器人[{}]复活，跟随杀手[{}]", nick, enemy.getNick());
            changeState(TankRobotState.FOLLOW);
        } else {
            changeToIdle();
            LogUtil.debug("机器人[{}]复活", nick);
        }
    }

    /**
     * 回收处理
     */
    void recycle() {
        changeState(SLEEP);
        room = null;
        pos = null;
        direction = 0;
        tankId = 0;
        speed = 0;
        alive = false;
        nextShotTime = 0;
        idleEndTime = 0;
        move.clear();
    }

    private void changeToIdle() {
        enemy = null;
        move.clear();
        changeState(IDLE);
    }

    public boolean isSleep() {
        return currentState() == null || SLEEP.equals(currentState());
    }

    @Override
    public int hashCode() {
        return (int) (roleId % 1000000);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TankRobot && ((TankRobot) obj).roleId == roleId;
    }

    @Override
    public int getRoomId() {
        return room.getRoomId();
    }
}
