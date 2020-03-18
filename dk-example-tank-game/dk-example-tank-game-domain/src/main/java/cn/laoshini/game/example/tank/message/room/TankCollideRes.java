package cn.laoshini.game.example.tank.message.room;

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
@ToString
@TankMessage(id = TankCollideRes.MESSAGE_ID)
public class TankCollideRes {
    public static final int MESSAGE_ID = TankConstants.MESSAGE_HEAD + TankConstants.TANK_COLLIDE_REQ + 1;

}
