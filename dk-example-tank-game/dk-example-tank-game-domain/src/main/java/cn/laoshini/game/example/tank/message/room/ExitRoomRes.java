package cn.laoshini.game.example.tank.message.room;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import cn.laoshini.game.example.tank.annotation.TankMessage;
import cn.laoshini.game.example.tank.constant.TankConstants;

/**
 * @author fagarine
 */
@Getter
@Setter
@Builder
@ToString
@TankMessage(id = ExitRoomRes.MESSAGE_ID)
public class ExitRoomRes {

    public static final int MESSAGE_ID = TankConstants.MESSAGE_HEAD + TankConstants.EXIT_ROOM_REQ + 1;
}
