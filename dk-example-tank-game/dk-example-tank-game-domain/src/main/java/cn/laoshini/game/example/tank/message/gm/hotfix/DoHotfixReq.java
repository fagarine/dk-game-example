package cn.laoshini.game.example.tank.message.gm.hotfix;

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
@TankMessage(id = DoHotfixReq.MESSAGE_ID, gm = true)
public class DoHotfixReq {

    public static final int MESSAGE_ID = TankConstants.GM_HEAD + TankConstants.DO_HOTFIX_REQ;
}
