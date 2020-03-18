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
@TankMessage(id = EnterRoomReq.MESSAGE_ID, desc = "进入房间消息")
public class EnterRoomReq {

    public static final int MESSAGE_ID = TankConstants.MESSAGE_HEAD + TankConstants.ENTER_ROOM_REQ;

    private Integer roomId;

    private Boolean create;

}
