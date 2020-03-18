package cn.laoshini.game.example.tank.message;

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
@TankMessage(id = HeartBeatRes.MESSAGE_ID)
public class HeartBeatRes {

    public static final int MESSAGE_ID = TankConstants.MESSAGE_HEAD + TankConstants.HEART_BEAT_REQ + 1;

    private Integer index;

    private Integer serverTime;

}
