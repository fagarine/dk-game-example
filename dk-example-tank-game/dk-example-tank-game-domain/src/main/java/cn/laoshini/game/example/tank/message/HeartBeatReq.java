package cn.laoshini.game.example.tank.message;

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
@TankMessage(id = HeartBeatReq.MESSAGE_ID)
public class HeartBeatReq {

    public static final int MESSAGE_ID = TankConstants.MESSAGE_HEAD + TankConstants.HEART_BEAT_REQ;

    private Integer index;

}
