package cn.laoshini.game.example.tank.dto;

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
public class RoomRankDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 排名
     */
    private Integer rank;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 角色名称
     */
    private String nick;

    /**
     * 角色得分
     */
    private Integer score;
}
