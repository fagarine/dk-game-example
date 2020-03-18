package cn.laoshini.game.example.tank.message.room;

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
@TankMessage(id = GetRoomsReq.MESSAGE_ID)
public class GetRoomsReq {

    public static final int MESSAGE_ID = TankConstants.MESSAGE_HEAD + TankConstants.GET_ROOMS_REQ;
}
