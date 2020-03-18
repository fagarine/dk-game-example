package cn.laoshini.game.example.tank.message.gm.module;

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
@TankMessage(id = RemoveModuleRes.MESSAGE_ID, gm = true)
public class RemoveModuleRes {

    public static final int MESSAGE_ID = TankConstants.GM_HEAD + TankConstants.REMOVE_MODULE_REQ + 1;
}
