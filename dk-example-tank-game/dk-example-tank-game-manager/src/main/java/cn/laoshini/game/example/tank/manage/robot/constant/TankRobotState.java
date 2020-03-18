package cn.laoshini.game.example.tank.manage.robot.constant;

import cn.laoshini.dk.robot.fsm.IFsmState;
import cn.laoshini.dk.util.LogUtil;
import cn.laoshini.game.example.tank.manage.robot.TankRobot;

/**
 * 坦克机器人逻辑状态枚举
 *
 * @author fagarine
 */
public enum TankRobotState implements IFsmState<TankRobot> {

    /**
     * 沉睡，未启用
     */
    SLEEP("沉睡"),

    /**
     * 空闲
     */
    IDLE("空闲") {
        @Override
        public void enter(TankRobot robot) {
            super.enter(robot);
            robot.idleEnter();
        }

        @Override
        public void refresh(TankRobot robot) {
            super.refresh(robot);

            long now = System.currentTimeMillis();
            if (now >= robot.getIdleEndTime()) {
                robot.idleEnd();
            }
        }
    },

    /**
     * 巡逻
     */
    PATROL("巡逻") {
        @Override
        public void refresh(TankRobot robot) {
            super.refresh(robot);
            robot.patrol();
        }
    },

    /**
     * 跟随
     */
    FOLLOW("跟随") {
        @Override
        public void refresh(TankRobot robot) {
            super.refresh(robot);
            robot.follow();
        }
    },

    /**
     * 攻击
     */
    ATTACK("攻击") {
        @Override
        public void refresh(TankRobot robot) {
            super.refresh(robot);
            robot.attack();
        }
    },

    /**
     * 死亡
     */
    DEAD("死亡") {
    },
    ;

    @Override
    public void enter(TankRobot robot) {
        LogUtil.debug("机器人[{}],id:[{}]进入[{}]状态", robot.getNick(), robot.getRoleId(), this.cnName);
    }

    @Override
    public void refresh(TankRobot robot) {
        LogUtil.debug("机器人[{}],id:[{}]执行[{}]状态逻辑", robot.getNick(), robot.getRoleId(), this.cnName);

    }

    @Override
    public void exit(TankRobot robot) {
        LogUtil.debug("机器人[{}],id:[{}]退出[{}]状态", robot.getNick(), robot.getRoleId(), this.cnName);
    }

    private String cnName;

    TankRobotState(String cnName) {
        this.cnName = cnName;
    }

    public String getCnName() {
        return cnName;
    }
}
