package cn.laoshni.game.example.chat.server.protobuf;

import cn.laoshni.game.example.chat.server.ProtobufUtil;

import cn.laoshini.dk.annotation.MessageHandle;
import cn.laoshini.dk.constant.GameCodeEnum;
import cn.laoshini.dk.domain.GameSubject;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.exception.MessageException;
import cn.laoshini.dk.net.handler.IMessageHandler;
import cn.laoshini.dk.net.session.AbstractSession;
import cn.laoshini.game.example.chat.constant.ChatConstants;
import cn.laoshini.game.example.chat.pb.ChatGameMessages;
import cn.laoshini.game.example.chat.server.manager.SessionHolder;

/**
 * @author fagarine
 */
@MessageHandle(id = ChatConstants.PRIVATE_CHAT_REQ_MESSAGE_ID, description = "私聊信息")
public class PrivateChatHandler implements IMessageHandler<ChatGameMessages.PrivateChatReq> {

    @Override
    public void action(ReqMessage<ChatGameMessages.PrivateChatReq> reqMessage, GameSubject subject)
            throws MessageException {
        AbstractSession session = SessionHolder.getSessionByName(reqMessage.getData().getName());
        if (session != null) {
            if (session.getId().equals(subject.getSession().getId())) {
                throw new MessageException(GameCodeEnum.PARAM_ERROR, "target.player.error", "不能给自己发私聊");
            }

            String content = reqMessage.getData().getContent();
            ChatGameMessages.PrivateChatRes res = ChatGameMessages.PrivateChatRes.newBuilder().setContent(content)
                    .build();
            subject.getSession().sendMessage(ProtobufUtil.buildBase(ChatConstants.PRIVATE_CHAT_RES_MESSAGE_ID, res));

            // 推送消息给目标用户
            String name = SessionHolder.getNameById(subject.getSession().getId());
            ChatGameMessages.ChatPush push = ChatGameMessages.ChatPush.newBuilder().setName(name).setContent(content)
                    .build();
            session.sendMessage(ProtobufUtil.buildBase(ChatConstants.PRIVATE_CHAT_PUSH_MESSAGE_ID, push));
        } else {
            throw new MessageException(GameCodeEnum.PLAYER_NOT_FOUND, "player.not.found", "未找到私聊目标玩家");
        }
    }

}
