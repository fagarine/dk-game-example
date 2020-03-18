package cn.laoshini.game.example.tank.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import cn.laoshini.dk.dao.TableKey;
import cn.laoshini.dk.dao.TableMapping;

/**
 * @author fagarine
 */
@Getter
@Setter
@ToString
@TableMapping(value = "p_account", description = "玩家帐号表")
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 帐号id，唯一
     */
    @TableKey(autoIncrement = true)
    private Integer accountId;
    /**
     * 玩家来源渠道号
     */
    private Integer platNo;
    /**
     * 如果是聚合渠道，记录子渠道号
     */
    private Integer childNo;
    /**
     * 玩家在来源渠道的平台id
     */
    private String platId;
    /**
     * 玩家最后登录的设备号
     */
    private String deviceNo;
    /**
     * 记录玩家最后登录的服务器id
     */
    private Integer serverId;
    /**
     * 如果玩家创建了角色，记录角色id
     */
    private Long roleId;
    /**
     * 玩家所属白名单类型
     */
    private Integer whiteName;
    /**
     * 记录玩家是否被禁，如果被禁，记录被禁类型
     */
    private Integer forbid;
    /**
     * 如果被禁，记录解禁时间
     */
    private Date liftTime;
    /**
     * 帐号创建时间
     */
    private Date createTime;
    /**
     * 记录最后一次登录时间
     */
    private Date loginTime;
}
