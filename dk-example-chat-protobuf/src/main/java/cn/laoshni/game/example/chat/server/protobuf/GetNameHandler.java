package cn.laoshni.game.example.chat.server.protobuf;

import java.util.concurrent.atomic.AtomicInteger;

import cn.laoshni.game.example.chat.server.ProtobufUtil;

import cn.laoshini.dk.annotation.MessageHandle;
import cn.laoshini.dk.domain.GameSubject;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.exception.MessageException;
import cn.laoshini.dk.net.handler.IMessageHandler;
import cn.laoshini.game.example.chat.constant.ChatConstants;
import cn.laoshini.game.example.chat.pb.ChatGameMessages;

/**
 * @author fagarine
 */
@MessageHandle(id = ChatConstants.GET_NAME_REQ_MESSAGE_ID, description = "获取一个未使用的名称", allowGuestRequest = true)
public class GetNameHandler implements IMessageHandler<ChatGameMessages.GetNameReq> {

    private static final AtomicInteger SEQUENCE = new AtomicInteger();

    @Override
    public void action(ReqMessage<ChatGameMessages.GetNameReq> reqMessage, GameSubject subject)
            throws MessageException {
        String name = ChatConstants.GAME_NAME + "用户" + SEQUENCE.incrementAndGet();
        ChatGameMessages.GetNameRes res = ChatGameMessages.GetNameRes.newBuilder().setName(name).build();
        subject.getSession().sendMessage(ProtobufUtil.buildBase(ChatConstants.GET_NAME_RES_MESSAGE_ID, res));
    }
}
