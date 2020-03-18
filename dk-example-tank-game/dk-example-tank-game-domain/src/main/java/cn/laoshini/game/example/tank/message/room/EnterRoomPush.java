package cn.laoshini.game.example.tank.message.room;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import cn.laoshini.game.example.tank.annotation.TankMessage;
import cn.laoshini.game.example.tank.constant.TankConstants;
import cn.laoshini.game.example.tank.domain.RoomRole;

/**
 * @author fagarine
 */
@Getter
@Setter
@Builder
@ToString
@TankMessage(id = EnterRoomPush.MESSAGE_ID, desc = "玩家进入房间推送消息")
public class EnterRoomPush {

    public static final int MESSAGE_ID = TankConstants.PUSH_HEAD + TankConstants.ENTER_ROOM_PUSH;

    /**
     * 进入房间的玩家信息
     */
    private RoomRole role;
}
