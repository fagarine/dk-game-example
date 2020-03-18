package cn.laoshini.game.example.chat.server.handler;

import cn.laoshini.dk.annotation.MessageHandle;
import cn.laoshini.dk.domain.GameSubject;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.domain.msg.RespMessage;
import cn.laoshini.dk.exception.MessageException;
import cn.laoshini.dk.net.handler.IMessageHandler;
import cn.laoshini.game.example.chat.message.PublicChatPush;
import cn.laoshini.game.example.chat.message.PublicChatReq;
import cn.laoshini.game.example.chat.message.PublicChatRes;
import cn.laoshini.game.example.chat.server.manager.SessionHolder;

/**
 * @author fagarine
 */
@MessageHandle(id = PublicChatReq.MESSAGE_ID, description = "公共聊天")
public class PublicChatHandler implements IMessageHandler<PublicChatReq> {

    @Override
    public void action(ReqMessage<PublicChatReq> reqMessage, GameSubject subject) throws MessageException {
        RespMessage<PublicChatRes> resp = new RespMessage<>();
        resp.setId(PublicChatRes.getId());
        resp.setData(new PublicChatRes());
        resp.getData().setContent(reqMessage.getData().getContent());
        subject.getSession().sendMessage(resp);

        // 推送消息给目标用户
        RespMessage<PublicChatPush> push = new RespMessage<>();
        push.setId(PublicChatPush.getId());
        push.setData(new PublicChatPush());
        push.getData().setName(SessionHolder.getNameById(subject.getSession().getId()));
        push.getData().setContent(reqMessage.getData().getContent());
        SessionHolder.pushToAll(push, subject.getSession().getId());
    }
}
