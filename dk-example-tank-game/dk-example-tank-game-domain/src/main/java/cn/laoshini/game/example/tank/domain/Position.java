package cn.laoshini.game.example.tank.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 坐标
 *
 * @author fagarine
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Position implements Serializable {

    private static final long serialVersionUID = 1L;

    private int x;

    private int y;

    public Position(Position pos) {
        this.x = pos.getX();
        this.y = pos.getY();
    }

    public void toPosition(Position pos) {
        this.x = pos.getX();
        this.y = pos.getY();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position) {
            Position pos = (Position) obj;
            return pos.x == x && pos.y == y;
        }
        return false;
    }
}
