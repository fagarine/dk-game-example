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
@TankMessage(id = BeginGameReq.MESSAGE_ID)
public class BeginGameReq {

    public static final int MESSAGE_ID = TankConstants.MESSAGE_HEAD + TankConstants.BEGIN_GAME_REQ;

    private Integer platNo;

    private Integer serverId;

    /**
     * 客户端设备号
     */
    private String deviceNo;

    /**
     * 客户端程序基础版本
     */
    private String baseVersion;

    /**
     * 客户端程序当前版本
     */
    private String version;

    private Boolean reconnect;
}
