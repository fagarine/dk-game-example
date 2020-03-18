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
@TankMessage(id = TankShotPush.MESSAGE_ID)
public class TankShotPush {
    public static final int MESSAGE_ID = TankConstants.PUSH_HEAD + TankConstants.TANK_SHOT_PUSH;

    private Integer bid;

    private Integer bulletId;

    private Integer direction;

    private Integer speed;

    private Position from;

    private Position target;

}
