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
@TankMessage(id = RoleLoginReq.MESSAGE_ID)
public class RoleLoginReq {

    public static final int MESSAGE_ID = TankConstants.MESSAGE_HEAD + TankConstants.ROLE_LOGIN_REQ;

    private Integer serverId;

    /**
     * 玩家昵称
     */
    private String nick;

    /**
     * 玩家选择的头像或者坦克类型
     */
    private Integer portrait;
}
