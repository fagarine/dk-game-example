package cn.laoshini.game.example.tank.message.room;

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
@TankMessage(id = PlayerDeadPush.MESSAGE_ID)
public class PlayerDeadPush {
    public static final int MESSAGE_ID = TankConstants.PUSH_HEAD + TankConstants.PLAYER_DEAD_PUSH;

    private int deadNum;

    private int waitSecond;
}
