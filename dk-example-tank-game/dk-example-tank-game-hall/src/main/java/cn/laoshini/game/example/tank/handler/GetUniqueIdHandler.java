package cn.laoshini.game.example.tank.handler;

import javax.annotation.Resource;

import cn.laoshini.dk.annotation.MessageHandle;
import cn.laoshini.dk.domain.GameSubject;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.exception.MessageException;
import cn.laoshini.dk.net.handler.IMessageHandler;
import cn.laoshini.game.example.tank.domain.TankPlayer;
import cn.laoshini.game.example.tank.message.plat.GetUniqueIdReq;
import cn.laoshini.game.example.tank.message.plat.GetUniqueIdRes;
import cn.laoshini.game.example.tank.service.IPlayerService;

/**
 * @author fagarine
 */
@MessageHandle(id = GetUniqueIdReq.MESSAGE_ID)
public class GetUniqueIdHandler implements IMessageHandler<GetUniqueIdReq> {

    @Resource
    private IPlayerService playerService;

    @Override
    public void action(ReqMessage<GetUniqueIdReq> reqMessage, GameSubject subject) throws MessageException {
        String uid = playerService.getUniqueId();
        TankPlayer player = (TankPlayer) subject;
        player.sendRespMessage(GetUniqueIdRes.MESSAGE_ID, GetUniqueIdRes.builder().uid(uid).build());
    }
}
