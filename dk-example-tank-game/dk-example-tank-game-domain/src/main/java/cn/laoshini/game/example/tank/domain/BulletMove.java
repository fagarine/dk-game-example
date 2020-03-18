package cn.laoshini.game.example.tank.domain;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 子弹移动轨迹信息
 *
 * @author fagarine
 */
@Getter
@Setter
@ToString
public class BulletMove implements Serializable {

    private static final long serialVersionUID = 1L;

    private static AtomicInteger bid = new AtomicInteger(1);

    private int id;
    private int roomId;
    private long roleId;
    private int bulletId;
    private Position curPos;
    private Position startPos;
    private Position targetPos;
    private long createTime;
    private long updateTime;
    /**
     * 最大存在时间，单位:ms
     */
    private int aliveTime;
    private long blastTime;
    private boolean blast;

    public BulletMove() {
    }

    public BulletMove(int roomId, long roleId, int bulletId, Position startPos, Position targetPos, int aliveTime) {
        this.roomId = roomId;
        this.roleId = roleId;
        this.bulletId = bulletId;
        this.curPos = new Position(startPos);
        this.startPos = new Position(startPos);
        this.targetPos = new Position(targetPos);
        this.createTime = System.currentTimeMillis();
        this.updateTime = createTime;
        this.aliveTime = aliveTime;
        this.blastTime = createTime + aliveTime;
        this.blast = false;
        this.id = bid.getAndIncrement();
    }

    /**
     * 子弹是否已运行到终点
     *
     * @return 返回判断结果
     */
    public boolean isAtTerminus() {
        return System.currentTimeMillis() >= blastTime;
    }
}
