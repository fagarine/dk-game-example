package cn.laoshini.game.example.tank.handler;

import javax.annotation.Resource;

import cn.laoshini.dk.annotation.MessageHandle;
import cn.laoshini.dk.domain.GameSubject;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.exception.MessageException;
import cn.laoshini.dk.net.handler.IMessageHandler;
import cn.laoshini.game.example.tank.domain.TankPlayer;
import cn.laoshini.game.example.tank.message.room.GetRoomRankReq;
import cn.laoshini.game.example.tank.service.IRoomService;

/**
 * @author fagarine
 */
@MessageHandle(id = GetRoomRankReq.MESSAGE_ID)
public class GetRoomRankHandler implements IMessageHandler<GetRoomRankReq> {

    @Resource
    private IRoomService roomService;

    @Override
    public void action(ReqMessage<GetRoomRankReq> reqMessage, GameSubject subject) throws MessageException {
        roomService.getRoomRank(reqMessage.getData(), (TankPlayer) subject);
    }
}
