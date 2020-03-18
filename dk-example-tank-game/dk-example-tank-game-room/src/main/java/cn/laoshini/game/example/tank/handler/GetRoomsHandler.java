package cn.laoshini.game.example.tank.handler;

import java.util.ArrayList;

import javax.annotation.Resource;

import cn.laoshini.dk.annotation.MessageHandle;
import cn.laoshini.dk.domain.GameSubject;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.exception.MessageException;
import cn.laoshini.dk.net.handler.IMessageHandler;
import cn.laoshini.game.example.tank.domain.TankPlayer;
import cn.laoshini.game.example.tank.message.room.GetRoomsReq;
import cn.laoshini.game.example.tank.message.room.GetRoomsRes;
import cn.laoshini.game.example.tank.service.IRoomService;

/**
 * @author fagarine
 */
@MessageHandle(id = GetRoomsReq.MESSAGE_ID)
public class GetRoomsHandler implements IMessageHandler<GetRoomsReq> {

    @Resource
    private IRoomService roomService;

    @Override
    public void action(ReqMessage<GetRoomsReq> reqMessage, GameSubject subject) throws MessageException {
        TankPlayer player = (TankPlayer) subject;
        player.sendRespMessage(GetRoomsRes.MESSAGE_ID,
                GetRoomsRes.builder().rooms(new ArrayList<>(roomService.getRooms())).build());
    }
}
