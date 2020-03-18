package cn.laoshini.game.example.tank.manage.robot;

import java.util.List;

import javax.annotation.Resource;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import cn.laoshini.dk.annotation.FunctionDependent;
import cn.laoshini.dk.generator.name.INameGenerator;
import cn.laoshini.dk.robot.fsm.IStateMachine;
import cn.laoshini.dk.util.LogUtil;
import cn.laoshini.game.example.tank.config.TankRobotConfig;
import cn.laoshini.game.example.tank.constant.TankConstants;
import cn.laoshini.game.example.tank.domain.Position;
import cn.laoshini.game.example.tank.domain.RoomRole;
import cn.laoshini.game.example.tank.entity.StaticTank;
import cn.laoshini.game.example.tank.manage.ini.StaticDataManager;
import cn.laoshini.game.example.tank.manage.room.Room;
import cn.laoshini.game.example.tank.manage.room.RoomDataManager;
import cn.laoshini.game.example.tank.manage.room.RoomRank;
import cn.laoshini.game.example.tank.message.room.EnterRoomPush;
import cn.laoshini.game.example.tank.message.room.TankDisappearPush;
import cn.laoshini.game.example.tank.util.PositionUtil;

/**
 * @author fagarine
 */
@Component
public class TankRobotMachine implements IStateMachine {

    @Resource
    private TankRobotConfig tankRobotConfig;

    @Resource
    private RobotManager robotManager;

    @Resource
    private RoomDataManager roomDataManager;

    @Resource
    private StaticDataManager staticDataManager;

    @FunctionDependent
    private INameGenerator nameGenerator;

    @Override
    public void initialize() {
        // 创建一批机器人对象
        List<String> names = nameGenerator.batchName(tankRobotConfig.getRobotMax());
        TankRobot robot;
        long roleIdBase = 888000000 + RandomUtils.nextInt(1, 10000);
        int roleIdGrow = 0;
        for (String name : names) {
            robot = new TankRobot();
            roleIdGrow += RandomUtils.nextInt(1, 100);
            robot.setRoleId(roleIdBase + roleIdGrow);
            robot.setNick(name);
            robot.setPortrait(TankConstants.INIT_PORTRAIT);
            robot.setRobot(true);
            robotManager.putRobot(robot);
        }
    }

    @Override
    public void tick() {
        List<TankRobot> robots = robotManager.getWorkRobots();
        if (!robots.isEmpty()) {
            // 房间内机器人执行逻辑
            for (TankRobot robot : robots) {
                try {
                    robot.tick();
                } catch (Exception e) {
                    LogUtil.error(String.format("机器人[%s]定时任务执行出错", robot.getNick()), e);
                }
            }
        }

        try {
            robotAllotTimerLogic();
        } catch (Exception e) {
            LogUtil.error("机器人分配逻辑执行出错", e);
        }
    }

    private void robotAllotTimerLogic() {
        boolean haveSleepRobot = true;
        for (Room room : roomDataManager.getAllRoom()) {
            if (room.isEmpty()) {
                // 已空闲的房间，机器人回收
                if (!room.getRobots().isEmpty()) {
                    for (int i = 0; i < room.getRobotNum(); i++) {
                        TankRobot robot = room.getRobots().remove(0);
                        robotRecycle(robot);
                    }
                }
            } else if (room.getPlayerNum() < tankRobotConfig.getRobotTrigger() && room.getRobotNum() < tankRobotConfig
                    .getRobotRoomMax()) {
                // 人少的房间，分配若干个机器人
                if (haveSleepRobot) {
                    int count = RandomUtils.nextInt(1, tankRobotConfig.getRobotRoomMax() - room.getRobotNum());
                    for (int i = 0; i < count; i++) {
                        TankRobot robot = robotManager.takeSleepRobot();
                        if (robot == null) {
                            // 没有多余的机器人了，这里可以添加一些处理策略，暂时不做处理
                            haveSleepRobot = false;
                            break;
                        }
                        robotEnterRoom(robot, room);
                    }
                }
            } else if (room.isBoisterous() && room.containsRobot()) {
                // 人多的房间，机器人退出，一次退出若干个
                int count = RandomUtils.nextInt(1, room.getRobotNum());
                for (int i = 0; i < count; i++) {
                    TankRobot robot = room.getRobots().remove(0);
                    robotRecycle(robot);
                }
            }
        }
    }

    private void robotRecycle(TankRobot robot) {
        robotManager.removeWorkRobotById(robot.getRoleId());
        robotExitRoom(robot);
        robot.recycle();
        robotManager.putRobot(robot);
    }

    private void robotEnterRoom(TankRobot robot, Room room) {
        robot.wakeUp();
        robot.setRoom(room);
        RoomRank rank = room.roleEnterRoom(robot, robot.getNick());

        StaticTank tank = staticDataManager.getTankById(TankConstants.INIT_TANK_ID);
        robot.setTankId(tank.getTankId());
        robot.setSpeed(TankConstants.INIT_SPEED);
        robot.setAlive(true);

        Position pos = PositionUtil
                .randomPosition(TankConstants.ROOM_X_LEN, TankConstants.ROOM_Y_LEN, TankConstants.ROOM_BORDER);
        robot.setPos(pos);
        robot.setDirection(RandomUtils.nextInt(0, 8));

        robotManager.putWorkRobot(robot);

        // 排行榜变更
        if (room.isInShowRank(rank.getRank())) {
            roomDataManager.pushRankListChange(room);
        }

        // 向其他人推送机器人进入房间
        roomDataManager.pushToAll(room, EnterRoomPush.MESSAGE_ID, EnterRoomPush.builder()
                .role(RoomRole.builder().roleId(robot.getRoleId()).nick(robot.getNick()).portrait(robot.getPortrait())
                        .speed(robot.getSpeed()).pos(pos).direction(robot.getDirection()).build()).build());
    }

    private void robotExitRoom(TankRobot robot) {
        Room room = robot.getRoom();
        int rankIndex = room.getRoleRankIndex(robot.getRoleId());
        room.roleExitRoom(robot);

        if (!room.isEmpty()) {
            // 推送机器人消失消息
            roomDataManager.pushToAll(room, TankDisappearPush.MESSAGE_ID,
                    TankDisappearPush.builder().roleIds(Lists.newArrayList(robot.getRoleId())).build());

            if (room.isInShowRank(rankIndex)) {
                // 推送排行榜变更消息
                roomDataManager.pushRankListChange(room);
            }
        }
    }
}
