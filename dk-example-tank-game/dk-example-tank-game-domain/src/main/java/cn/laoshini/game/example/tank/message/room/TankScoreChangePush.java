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
@TankMessage(id = TankScoreChangePush.MESSAGE_ID)
public class TankScoreChangePush {
    public static final int MESSAGE_ID = TankConstants.PUSH_HEAD + TankConstants.TANK_SCORE_CHANGE_PUSH;

    private Integer rank;

    private Integer score;

    private Integer killNum;

    private Integer deadNum;

}
