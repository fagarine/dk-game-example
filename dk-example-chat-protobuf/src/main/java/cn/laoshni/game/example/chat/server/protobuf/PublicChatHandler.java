package cn.laoshni.game.example.chat.server.protobuf;

import cn.laoshni.game.example.chat.server.ProtobufUtil;

import cn.laoshini.dk.annotation.MessageHandle;
import cn.laoshini.dk.domain.GameSubject;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.domain.msg.RespMessage;
import cn.laoshini.dk.exception.MessageException;
import cn.laoshini.dk.net.handler.IMessageHandler;
import cn.laoshini.game.example.chat.constant.ChatConstants;
import cn.laoshini.game.example.chat.pb.ChatGameMessages;
import cn.laoshini.game.example.chat.server.manager.SessionHolder;

/**
 * @author fagarine
 */
@MessageHandle(id = ChatConstants.PUBLIC_CHAT_REQ_MESSAGE_ID, description = "公共聊天")
public class PublicChatHandler implements IMessageHandler<ChatGameMessages.PublicChatReq> {

    @Override
    public void action(ReqMessage<ChatGameMessages.PublicChatReq> reqMessage, GameSubject subject)
            throws MessageException {
        String content = reqMessage.getData().getContent();
        ChatGameMessages.PublicChatRes res = ChatGameMessages.PublicChatRes.newBuilder().setContent(content).build();
        subject.getSession().sendMessage(ProtobufUtil.buildBase(ChatConstants.PUBLIC_CHAT_RES_MESSAGE_ID, res));

        // 推送消息给目标用户
        String name = SessionHolder.getNameById(subject.getSession().getId());
        // 尝试以AbstractMessage<com.google.protobuf.Message>的形式发送消息对象，默认protobuf编码类BaseProtobufMessageEncoder兼容了这种消息形式
        RespMessage<ChatGameMessages.ChatPush> push = new RespMessage<>();
        push.setId(ChatConstants.PUBLIC_CHAT_PUSH_MESSAGE_ID);
        push.setData(ChatGameMessages.ChatPush.newBuilder().setName(name).setContent(content).build());
        SessionHolder.pushToAll(push, subject.getSession().getId());
    }
}
