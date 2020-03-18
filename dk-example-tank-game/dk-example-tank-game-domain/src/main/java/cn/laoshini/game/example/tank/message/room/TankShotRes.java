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
@TankMessage(id = TankShotRes.MESSAGE_ID)
public class TankShotRes {
    public static final int MESSAGE_ID = TankConstants.MESSAGE_HEAD + TankConstants.TANK_SHOT_REQ + 1;

    private Integer bid;

    private Integer bulletId;

    private Integer direction;

    private Integer speed;

    private Position from;

    private Position target;

}
