package cn.laoshini.game.example.tank.handler;

import javax.annotation.Resource;

import cn.laoshini.dk.annotation.MessageHandle;
import cn.laoshini.dk.domain.GameSubject;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.exception.MessageException;
import cn.laoshini.dk.net.handler.IMessageHandler;
import cn.laoshini.game.example.tank.domain.TankPlayer;
import cn.laoshini.game.example.tank.message.room.TankShotReq;
import cn.laoshini.game.example.tank.service.IRoomService;

/**
 * @author fagarine
 */
@MessageHandle(id = TankShotReq.MESSAGE_ID)
public class TankShotHandler implements IMessageHandler<TankShotReq> {

    @Resource
    private IRoomService roomService;

    @Override
    public void action(ReqMessage<TankShotReq> reqMessage, GameSubject subject) throws MessageException {
        roomService.tankShot(reqMessage.getData(), (TankPlayer) subject);
    }
}
