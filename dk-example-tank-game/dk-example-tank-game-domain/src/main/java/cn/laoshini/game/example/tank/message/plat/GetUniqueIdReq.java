package cn.laoshini.game.example.tank.message.plat;

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
@TankMessage(id = GetUniqueIdReq.MESSAGE_ID)
public class GetUniqueIdReq {

    public static final int MESSAGE_ID = TankConstants.PLAT_MESSAGE_HEAD + TankConstants.GET_UNIQUE_ID_REQ;

}
