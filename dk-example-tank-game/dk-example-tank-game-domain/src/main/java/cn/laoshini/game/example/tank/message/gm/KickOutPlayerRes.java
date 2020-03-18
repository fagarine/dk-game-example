package cn.laoshini.game.example.tank.message.gm;

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
@TankMessage(id = KickOutPlayerRes.MESSAGE_ID, gm = true)
public class KickOutPlayerRes {

    public static final int MESSAGE_ID = TankConstants.GM_HEAD + TankConstants.KICK_OUT_PLAYER_REQ + 1;
}
