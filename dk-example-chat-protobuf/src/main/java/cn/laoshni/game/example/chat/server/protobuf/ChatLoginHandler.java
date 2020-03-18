package cn.laoshni.game.example.chat.server.protobuf;

import cn.laoshni.game.example.chat.server.ProtobufUtil;

import cn.laoshini.dk.annotation.MessageHandle;
import cn.laoshini.dk.constant.GameCodeEnum;
import cn.laoshini.dk.domain.GameSubject;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.exception.MessageException;
import cn.laoshini.dk.net.handler.IMessageHandler;
import cn.laoshini.dk.net.session.AbstractSession;
import cn.laoshini.dk.util.StringUtil;
import cn.laoshini.game.example.chat.constant.ChatConstants;
import cn.laoshini.game.example.chat.pb.ChatGameMessages;
import cn.laoshini.game.example.chat.server.manager.SessionHolder;

/**
 * @author fagarine
 */
@MessageHandle(id = ChatConstants.LOGIN_REQ_MESSAGE_ID, description = "游客登录", allowGuestRequest = true)
public class ChatLoginHandler implements IMessageHandler<ChatGameMessages.ChatLoginReq> {
    @Override
    public void action(ReqMessage<ChatGameMessages.ChatLoginReq> reqMessage, GameSubject subject)
            throws MessageException {
        AbstractSession session = subject.getSession();
        String name = reqMessage.getData().getName();
        if (StringUtil.isEmptyString(name)) {
            throw new MessageException(GameCodeEnum.LOGIN_NAME_ERROR, "login.name.empty", "玩家名称不能为空");
        }

        if (SessionHolder.contains(name)) {
            throw new MessageException(GameCodeEnum.LOGIN_NAME_DUPLICATED, "login.name.duplicated", "玩家名称成功");
        }

        SessionHolder.register(name, session);

        ChatGameMessages.ChatLoginRes res = ChatGameMessages.ChatLoginRes.newBuilder().build();
        session.sendMessage(ProtobufUtil.buildBase(ChatConstants.LOGIN_RES_MESSAGE_ID, res));
    }
}
