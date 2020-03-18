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
@TankMessage(id = TankMovePush.MESSAGE_ID)
public class TankMovePush {
    public static final int MESSAGE_ID = TankConstants.PUSH_HEAD + TankConstants.TANK_MOVE_PUSH;

    private Long roleId;

    private Position pos;

    private Position target;

    private int direction;

}
