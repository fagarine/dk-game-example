package cn.laoshini.game.example.tank.handler;

import javax.annotation.Resource;

import cn.laoshini.dk.annotation.MessageHandle;
import cn.laoshini.dk.domain.GameSubject;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.exception.MessageException;
import cn.laoshini.dk.net.handler.IMessageHandler;
import cn.laoshini.game.example.tank.domain.TankPlayer;
import cn.laoshini.game.example.tank.message.room.TankMoveReq;
import cn.laoshini.game.example.tank.service.IRoomService;

/**
 * @author fagarine
 */
@MessageHandle(id = TankMoveReq.MESSAGE_ID)
public class TankMoveHandler implements IMessageHandler<TankMoveReq> {

    @Resource
    private IRoomService roomService;

    @Override
    public void action(ReqMessage<TankMoveReq> reqMessage, GameSubject subject) throws MessageException {
        roomService.tankMove(reqMessage.getData(), (TankPlayer) subject);
    }
}
