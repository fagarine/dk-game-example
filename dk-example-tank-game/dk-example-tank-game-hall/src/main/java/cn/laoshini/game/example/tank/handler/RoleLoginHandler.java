package cn.laoshini.game.example.tank.handler;

import javax.annotation.Resource;

import cn.laoshini.dk.annotation.MessageHandle;
import cn.laoshini.dk.domain.GameSubject;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.exception.MessageException;
import cn.laoshini.dk.net.handler.IMessageHandler;
import cn.laoshini.game.example.tank.domain.TankPlayer;
import cn.laoshini.game.example.tank.message.RoleLoginReq;
import cn.laoshini.game.example.tank.service.IPlayerService;

/**
 * @author fagarine
 */
@MessageHandle(id = RoleLoginReq.MESSAGE_ID)
public class RoleLoginHandler implements IMessageHandler<RoleLoginReq> {

    @Resource
    private IPlayerService playerService;

    @Override
    public void action(ReqMessage<RoleLoginReq> reqMessage, GameSubject subject) throws MessageException {
        playerService.roleLogin(reqMessage.getData(), (TankPlayer) subject);
    }
}
