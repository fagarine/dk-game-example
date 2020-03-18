package cn.laoshini.game.example.tank.message.room;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import cn.laoshini.game.example.tank.annotation.TankMessage;
import cn.laoshini.game.example.tank.constant.TankConstants;
import cn.laoshini.game.example.tank.domain.Position;

/**
 * @author fagarine
 */
@Getter
@Setter
@ToString
@TankMessage(id = TankCollideReq.MESSAGE_ID)
public class TankCollideReq {
    public static final int MESSAGE_ID = TankConstants.MESSAGE_HEAD + TankConstants.TANK_COLLIDE_REQ;

    private Position pos;

    private long roleId;

    private int bid;
}
