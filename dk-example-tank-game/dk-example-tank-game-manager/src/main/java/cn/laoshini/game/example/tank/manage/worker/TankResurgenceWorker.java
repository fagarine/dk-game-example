package cn.laoshini.game.example.tank.manage.worker;

import org.apache.commons.lang3.RandomUtils;

import cn.laoshini.dk.common.SpringContextHolder;
import cn.laoshini.game.example.tank.constant.TankConstants;
import cn.laoshini.game.example.tank.domain.Position;
import cn.laoshini.game.example.tank.domain.TankPlayer;
import cn.laoshini.game.example.tank.manage.robot.TankRobot;
import cn.laoshini.game.example.tank.manage.room.Room;
import cn.laoshini.game.example.tank.manage.room.RoomDataManager;
import cn.laoshini.game.example.tank.message.room.TankAppearPush;

/**
 * @author fagarine
 */
public class TankResurgenceWorker implements Runnable {

    private static RoomDataManager roomDataManager = SpringContextHolder.getBean(RoomDataManager.class);

    private TankPlayer player;

    private Room room;

    public TankResurgenceWorker(TankPlayer player, Room room) {
        this.player = player;
        this.room = room;
    }

    @Override
    public void run() {
        if (player.isAlive()) {
            return;
        }

        // 随机玩家坐标
        int x = RandomUtils.nextInt(0, TankConstants.ROOM_X_LEN);
        int y = RandomUtils.nextInt(0, TankConstants.ROOM_Y_LEN);
        Position pos = new Position(x, y);
        player.setPos(pos);
        player.setDirection(0);
        player.setAlive(true);

        roomDataManager.pushToAll(room, TankAppearPush.MESSAGE_ID,
                TankAppearPush.builder().roleId(player.getRoleId()).pos(pos).direction(player.getDirection()).build());

        if (player instanceof TankRobot) {
            TankRobot robot = (TankRobot) player;
            if (!robot.isSleep()) {
                robot.resurgence();
            }
        }
    }
}
