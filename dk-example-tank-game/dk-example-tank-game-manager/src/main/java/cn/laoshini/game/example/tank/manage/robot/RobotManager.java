package cn.laoshini.game.example.tank.manage.robot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

import cn.laoshini.dk.util.CollectionUtil;

/**
 * @author fagarine
 */
@Component
public class RobotManager {

    private Queue<TankRobot> sleepRobots = new ConcurrentLinkedQueue<>();

    private Map<Long, TankRobot> idToWorkRobot = new ConcurrentHashMap<>();

    public void putRobots(Collection<TankRobot> robots) {
        if (CollectionUtil.isNotEmpty(robots)) {
            for (TankRobot robot : robots) {
                sleepRobots.offer(robot);
            }
        }
    }

    public void putRobot(TankRobot robot) {
        sleepRobots.offer(robot);
    }

    public TankRobot takeSleepRobot() {
        return sleepRobots.poll();
    }

    public void putWorkRobot(TankRobot robot) {
        idToWorkRobot.put(robot.getRoleId(), robot);
    }

    public List<TankRobot> getWorkRobots() {
        return new ArrayList<>(idToWorkRobot.values());
    }

    public void removeWorkRobotById(long roleId) {
        idToWorkRobot.remove(roleId);
    }

    public TankRobot getById(long roleId) {
        return idToWorkRobot.get(roleId);
    }
}
