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
@TankMessage(id = GetNamesReq.MESSAGE_ID)
public class GetNamesReq {

    public static final int MESSAGE_ID = TankConstants.MESSAGE_HEAD + TankConstants.GET_NAMES_REQ;
}
