package cn.laoshini.game.example.tank.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author fagarine
 */
@Getter
@Setter
@ToString
public class MovePath {

    private Position source;

    private Position target;

    private Position current;

    private int speed;

    private long beginTime;

    private boolean moveEnd = true;

    public void clear() {
        source = null;
        target = null;
        current = null;
        speed = 0;
        beginTime = 0;
        moveEnd = true;
    }

}
