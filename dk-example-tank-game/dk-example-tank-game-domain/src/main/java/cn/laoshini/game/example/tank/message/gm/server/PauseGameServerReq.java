package cn.laoshini.game.example.tank.message.gm.server;

import java.util.Date;

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
@TankMessage(id = PauseGameServerReq.MESSAGE_ID, gm = true)
public class PauseGameServerReq {

    public static final int MESSAGE_ID = TankConstants.GM_HEAD + TankConstants.PAUSE_GAME_SERVER_REQ;

    private String tips;

    private Date openTime;
}
