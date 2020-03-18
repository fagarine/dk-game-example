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
@TableMapping(value = "s_bullet", description = "子弹配置表")
public class StaticBullet implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableKey
    private int bulletId;
    private int bulletType;
    private int distance;
    private int speed;
    private int blastRadius;
    private int coolTime;
    private int width;
    private int height;
    private int imgWidth;
    private int imgHeight;
}
