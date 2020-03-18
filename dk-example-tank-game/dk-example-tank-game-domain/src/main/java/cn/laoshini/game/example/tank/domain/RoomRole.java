package cn.laoshini.game.example.tank.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 房间内的玩家信息
 *
 * @author fagarine
 */
@Getter
@Setter
@Builder
@ToString
public class RoomRole {

    /**
     * 玩家的角色id
     */
    private Long roleId;

    /**
     * 玩家名字
     */
    private String nick;

    /**
     * 玩家头像或者坦克类型
     */
    private Integer portrait;

    /**
     * 玩家移动速度
     */
    private Integer speed;

    /**
     * 玩家坐标
     */
    private Position pos;

    /**
     * 玩家朝向，0为默认朝向
     */
    private Integer direction;

}
