package cn.laoshini.game.example.tank.handler;

import javax.annotation.Resource;

import cn.laoshini.dk.annotation.MessageHandle;
import cn.laoshini.dk.domain.GameSubject;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.exception.MessageException;
import cn.laoshini.dk.net.handler.IMessageHandler;
import cn.laoshini.game.example.tank.domain.TankPlayer;
import cn.laoshini.game.example.tank.message.room.ExitRoomReq;
import cn.laoshini.game.example.tank.service.IRoomService;

/**
 * @author fagarine
 */
@MessageHandle(id = ExitRoomReq.MESSAGE_ID)
public class ExitRoomHandler implements IMessageHandler<ExitRoomReq> {

    @Resource
    private IRoomService roomService;

    @Override
    public void action(ReqMessage<ExitRoomReq> reqMessage, GameSubject subject) throws MessageException {
        roomService.exitRoom(reqMessage.getData(), (TankPlayer) subject);
    }
}
