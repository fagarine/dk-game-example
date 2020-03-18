package cn.laoshini.game.example.tank.handler;

import javax.annotation.Resource;

import cn.laoshini.dk.annotation.MessageHandle;
import cn.laoshini.dk.domain.GameSubject;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.exception.MessageException;
import cn.laoshini.dk.net.handler.IMessageHandler;
import cn.laoshini.game.example.tank.domain.TankPlayer;
import cn.laoshini.game.example.tank.message.BeginGameReq;
import cn.laoshini.game.example.tank.service.IPlayerService;

/**
 * @author fagarine
 */
@MessageHandle(id = BeginGameReq.MESSAGE_ID)
public class BeginGameHandler implements IMessageHandler<BeginGameReq> {

    @Resource
    private IPlayerService playerService;

    @Override
    public void action(ReqMessage<BeginGameReq> reqMessage, GameSubject subject) throws MessageException {
        playerService.beginGame(reqMessage.getData(), (TankPlayer) subject);
    }
}
