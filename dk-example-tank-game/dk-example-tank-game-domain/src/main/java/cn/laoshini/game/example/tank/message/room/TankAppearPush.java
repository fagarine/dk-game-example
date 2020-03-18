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
@TankMessage(id = TankAppearPush.MESSAGE_ID)
public class TankAppearPush {
    public static final int MESSAGE_ID = TankConstants.PUSH_HEAD + TankConstants.TANK_APPEAR_PUSH;

    private Long roleId;

    private Position pos;

    private Integer direction;
}
