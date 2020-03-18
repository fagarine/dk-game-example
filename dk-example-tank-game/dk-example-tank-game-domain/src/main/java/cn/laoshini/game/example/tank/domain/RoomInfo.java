package cn.laoshini.game.example.tank.domain;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author fagarine
 */
@Getter
@Setter
@Builder
@ToString
public class RoomInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 房间号
     */
    private Integer roomId;

    /**
     * 房间名称
     */
    private String roomName;

    /**
     * 当前玩家数
     */
    private Integer roleNum;

    /**
     * 房间最大容纳玩家数量
     */
    private Integer maxNum;

    /**
     * 是否需要密码
     */
    private boolean secret;
}
