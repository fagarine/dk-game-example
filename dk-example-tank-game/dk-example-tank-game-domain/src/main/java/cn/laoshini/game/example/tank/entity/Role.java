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
@TableMapping(value = "p_role", description = "玩家帐号表")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableKey
    private Long roleId;
    private String nick;
    private Integer portrait;
    private Integer roleLv;
    private Integer exp;
    private Integer vip;
    private Integer recharge;
    private Integer gold;
    private Integer goldCost;
    private Integer goldGive;
    private Integer roleState;
    private Date goldTime;
    private Date onTime;
    private Integer olTime;
    private Date offTime;
    private Date createTime;
}
