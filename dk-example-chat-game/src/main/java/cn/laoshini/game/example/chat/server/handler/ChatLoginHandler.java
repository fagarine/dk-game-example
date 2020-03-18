package cn.laoshini.game.example.chat.server.handler;

import cn.laoshini.dk.annotation.MessageHandle;
import cn.laoshini.dk.constant.GameCodeEnum;
import cn.laoshini.dk.domain.GameSubject;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.domain.msg.RespMessage;
import cn.laoshini.dk.exception.MessageException;
import cn.laoshini.dk.net.handler.IMessageHandler;
import cn.laoshini.dk.net.session.AbstractSession;
import cn.laoshini.dk.util.StringUtil;
import cn.laoshini.game.example.chat.message.ChatLoginReq;
import cn.laoshini.game.example.chat.message.ChatLoginRes;
import cn.laoshini.game.example.chat.server.manager.SessionHolder;

/**
 * @author fagarine
 */
@MessageHandle(id = ChatLoginReq.MESSAGE_ID, description = "游客登录", allowGuestRequest = true)
public class ChatLoginHandler implements IMessageHandler<ChatLoginReq> {
    @Override
    public void action(ReqMessage<ChatLoginReq> reqMessage, GameSubject subject) throws MessageException {
        AbstractSession session = subject.getSession();
        String name = reqMessage.getData().getName();
        if (StringUtil.isEmptyString(name)) {
            throw new MessageException(GameCodeEnum.LOGIN_NAME_ERROR, "login.name.empty", "玩家名称不能为空");
        }

        if (SessionHolder.contains(name)) {
            throw new MessageException(GameCodeEnum.LOGIN_NAME_DUPLICATED, "login.name.duplicated", "玩家名称成功");
        }

        SessionHolder.register(name, session);

        RespMessage<ChatLoginRes> resp = new RespMessage<>();
        resp.setId(ChatLoginRes.getId());
        resp.setData(new ChatLoginRes());
        session.sendMessage(resp);
    }
}
