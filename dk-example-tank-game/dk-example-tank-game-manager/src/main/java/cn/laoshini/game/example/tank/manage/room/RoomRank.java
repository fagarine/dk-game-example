package cn.laoshini.game.example.tank.manage.room;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author fagarine
 */
@Getter
@Setter
@ToString
public class RoomRank implements Comparable<RoomRank> {

    private long roleId;

    private String nick;

    private int killNum;

    private int deadNum;

    private int score;

    private int rankTime;

    private int rank;

    public void addKill(int addKill) {
        this.killNum += addKill;
    }

    public void addDead(int addDead) {
        this.deadNum += addDead;
    }

    public void addScore(int addScore) {
        this.score += addScore;
    }

    @Override
    public int compareTo(RoomRank o) {
        if (this.score > o.score) {
            return -1;
        } else if (this.score < o.score) {
            return 1;
        } else {
            if (this.rankTime < o.getRankTime()) {
                return -1;
            } else if (this.rankTime > o.getRankTime()) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int hashCode() {
        return (int) (roleId & 1000000);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RoomRank && ((RoomRank) obj).roleId == roleId;
    }
}
