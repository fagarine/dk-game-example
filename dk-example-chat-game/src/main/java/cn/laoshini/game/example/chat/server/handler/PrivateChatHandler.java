package cn.laoshini.game.example.chat.server.handler;

import cn.laoshini.dk.annotation.MessageHandle;
import cn.laoshini.dk.constant.GameCodeEnum;
import cn.laoshini.dk.domain.GameSubject;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.domain.msg.RespMessage;
import cn.laoshini.dk.exception.MessageException;
import cn.laoshini.dk.net.handler.IMessageHandler;
import cn.laoshini.dk.net.session.AbstractSession;
import cn.laoshini.game.example.chat.message.PrivateChatPush;
import cn.laoshini.game.example.chat.message.PrivateChatReq;
import cn.laoshini.game.example.chat.message.PrivateChatRes;
import cn.laoshini.game.example.chat.server.manager.SessionHolder;

/**
 * @author fagarine
 */
@MessageHandle(id = PrivateChatReq.MESSAGE_ID, description = "私聊信息")
public class PrivateChatHandler implements IMessageHandler<PrivateChatReq> {

    @Override
    public void action(ReqMessage<PrivateChatReq> reqMessage, GameSubject subject) throws MessageException {
        AbstractSession session = SessionHolder.getSessionByName(reqMessage.getData().getName());
        if (session != null) {
            if (session.getId().equals(subject.getSession().getId())) {
                throw new MessageException(GameCodeEnum.PARAM_ERROR, "target.player.error", "不能给自己发私聊");
            }

            RespMessage<PrivateChatRes> resp = new RespMessage<>();
            resp.setId(PrivateChatRes.getId());
            resp.setData(new PrivateChatRes());
            resp.getData().setContent(reqMessage.getData().getContent());
            subject.getSession().sendMessage(resp);

            // 推送消息给目标用户
            RespMessage<PrivateChatPush> push = new RespMessage<>();
            push.setId(PrivateChatPush.getId());
            push.setData(new PrivateChatPush());
            push.getData().setName(SessionHolder.getNameById(subject.getSession().getId()));
            push.getData().setContent(reqMessage.getData().getContent());
            session.sendMessage(push);
        } else {
            throw new MessageException(GameCodeEnum.PLAYER_NOT_FOUND, "player.not.found", "未找到私聊目标玩家");
        }
    }

}
