package cn.laoshini.game.example.tank.message.room;

import lombok.Builder;
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
@Builder
@ToString
@TankMessage(id = TankMoveRes.MESSAGE_ID)
public class TankMoveRes {
    public static final int MESSAGE_ID = TankConstants.MESSAGE_HEAD + TankConstants.TANK_MOVE_REQ + 1;

    private Position pos;

    private Position target;

    private int direction;
}
