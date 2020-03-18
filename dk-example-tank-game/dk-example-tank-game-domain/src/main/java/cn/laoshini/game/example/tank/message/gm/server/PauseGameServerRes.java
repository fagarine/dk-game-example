package cn.laoshini.game.example.tank.message.gm.server;

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
@TankMessage(id = PauseGameServerRes.MESSAGE_ID, gm = true)
public class PauseGameServerRes {

    public static final int MESSAGE_ID = TankConstants.GM_HEAD + TankConstants.PAUSE_GAME_SERVER_REQ + 1;
}
