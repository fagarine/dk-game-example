package cn.laoshini.game.example.tank.message.room;

import java.util.List;

import lombok.Builder;
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
@Builder
@ToString
@TankMessage(id = TankDisappearPush.MESSAGE_ID)
public class TankDisappearPush {
    public static final int MESSAGE_ID = TankConstants.PUSH_HEAD + TankConstants.TANK_DISAPPEAR_PUSH;

    /**
     * 消息的玩家id
     */
    private List<Long> roleIds;

}
