package cn.laoshini.game.example.tank.common;

import cn.laoshini.dk.exception.MessageException;
import cn.laoshini.game.example.tank.constant.GameError;

/**
 * @author fagarine
 */
public class TankException extends MessageException {

    private GameError gameError;

    public TankException(GameError errorCode, String message) {
        super(errorCode.getCode(), message);
        this.gameError = errorCode;
    }

    public GameError getGameError() {
        return gameError;
    }

    public void setGameError(GameError gameError) {
        this.gameError = gameError;
    }
}
