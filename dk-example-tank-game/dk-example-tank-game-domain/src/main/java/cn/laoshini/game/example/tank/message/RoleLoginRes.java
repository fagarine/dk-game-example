package cn.laoshini.game.example.tank.message;

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
@TankMessage(id = RoleLoginRes.MESSAGE_ID)
public class RoleLoginRes {

    public static final int MESSAGE_ID = TankConstants.MESSAGE_HEAD + TankConstants.ROLE_LOGIN_REQ + 1;

    private Long roleId;

    /**
     * 玩家昵称
     */
    private String nick;

    /**
     * 玩家选择的头像或者坦克类型
     */
    private Integer portrait;
}
