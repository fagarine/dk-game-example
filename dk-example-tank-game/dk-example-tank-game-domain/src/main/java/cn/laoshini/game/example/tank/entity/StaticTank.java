package cn.laoshini.game.example.tank.entity;

import java.io.Serializable;

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
@TableMapping(value = "s_tank", description = "子弹配置表")
public class StaticTank implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableKey
    private int tankId;

    private int radius;

    private int halfWidth;

    private int halfHeight;
}
