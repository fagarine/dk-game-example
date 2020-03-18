package cn.laoshini.game.example.chat.server.handler;

import java.util.concurrent.atomic.AtomicInteger;

import cn.laoshini.dk.annotation.MessageHandle;
import cn.laoshini.dk.domain.GameSubject;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.domain.msg.RespMessage;
import cn.laoshini.dk.exception.MessageException;
import cn.laoshini.dk.net.handler.IMessageHandler;
import cn.laoshini.game.example.chat.constant.ChatConstants;
import cn.laoshini.game.example.chat.message.GetNameReq;
import cn.laoshini.game.example.chat.message.GetNameRes;

/**
 * @author fagarine
 */
@MessageHandle(id = GetNameReq.MESSAGE_ID, description = "获取一个未使用的名称", allowGuestRequest = true)
public class GetNameHandler implements IMessageHandler<GetNameReq> {

    private static final AtomicInteger SEQUENCE = new AtomicInteger();

    @Override
    public void action(ReqMessage<GetNameReq> reqMessage, GameSubject subject) throws MessageException {
        RespMessage<GetNameRes> resp = new RespMessage<>();
        resp.setId(GetNameRes.getId());
        resp.setData(new GetNameRes());
        resp.getData().setName(ChatConstants.GAME_NAME + "用户" + SEQUENCE.incrementAndGet());
        subject.getSession().sendMessage(resp);
    }
}
