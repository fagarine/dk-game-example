package cn.laoshini.game.example.tank.manage.worker;

import cn.laoshini.dk.constant.GameCodeEnum;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.executor.AbstractOrderedWorker;
import cn.laoshini.dk.net.MessageHandlerHolder;
import cn.laoshini.game.example.tank.common.TankException;
import cn.laoshini.game.example.tank.domain.TankPlayer;

/**
 * @author fagarine
 */
public class ReceivedMessageWorker extends AbstractOrderedWorker {

    private ReqMessage<Object> reqMessage;

    private TankPlayer player;

    public ReceivedMessageWorker(ReqMessage<Object> reqMessage, TankPlayer player) {
        this.reqMessage = reqMessage;
        this.player = player;
    }

    @Override
    protected void action() {
        try {
            MessageHandlerHolder.doMessageHandler(reqMessage, player);
        } catch (TankException e) {
            player.sendAsErrorRespMessage(reqMessage.getId() + 1, e.getErrorCode(), e.getGameError().getDesc());
        } catch (Throwable t) {
            player.sendAsErrorRespMessage(reqMessage.getId() + 1, GameCodeEnum.UNKNOWN_ERROR.getCode(), "未知错误");
        }
    }
}
