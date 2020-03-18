package cn.laoshini.game.example.tank.message;

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
@TankMessage(id = BeginGameRes.MESSAGE_ID)
public class BeginGameRes {

    public static final int MESSAGE_ID = TankConstants.MESSAGE_HEAD + TankConstants.BEGIN_GAME_REQ + 1;

    /**
     * 角色未创建时，传递一些随机名字；如果这个设备登录过，上次玩家使用的名字会出现在最前面
     */
    private List<String> names;

    /**
     * 角色状态：详见{@link cn.laoshini.game.example.tank.constant.RoleState}
     */
    private Integer state;

    /**
     * 服务器当前时间
     */
    private Integer serverTime;
}
