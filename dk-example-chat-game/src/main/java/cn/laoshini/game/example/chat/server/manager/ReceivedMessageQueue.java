package cn.laoshini.game.example.chat.server.manager;

import cn.laoshini.dk.constant.GameCodeEnum;
import cn.laoshini.dk.domain.GameSubject;
import cn.laoshini.dk.domain.Player;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.domain.msg.RespMessage;
import cn.laoshini.dk.exception.MessageException;
import cn.laoshini.dk.executor.AbstractOrderedWorker;
import cn.laoshini.dk.executor.IOrderedExecutor;
import cn.laoshini.dk.executor.OrderedQueuePoolExecutor;
import cn.laoshini.dk.net.MessageHandlerHolder;
import cn.laoshini.dk.net.handler.IHttpMessageHandler;
import cn.laoshini.dk.net.handler.IMessageHandler;
import cn.laoshini.dk.net.session.AbstractSession;
import cn.laoshini.dk.util.LogUtil;
import cn.laoshini.game.example.chat.message.ChatLoginReq;
import cn.laoshini.game.example.chat.message.GetNameReq;

/**
 * @author fagarine
 */
public enum ReceivedMessageQueue {
    /**
     * 使用枚举实现单例
     */
    INSTANCE;

    private static final IOrderedExecutor<Long> MESSAGE_EXECUTOR = new OrderedQueuePoolExecutor("chat-received-message",
            3, Integer.MAX_VALUE);

    public static void addMessage(AbstractSession session, ReqMessage<Object> message) {
        MESSAGE_EXECUTOR.addTask(session.getId(), new MessageHandleWorker(session, message));
    }

    /**
     * 消息处理任务线程
     */
    private static class MessageHandleWorker extends AbstractOrderedWorker {

        private AbstractSession session;

        private ReqMessage<Object> message;

        public MessageHandleWorker(AbstractSession session, ReqMessage<Object> message) {
            this.session = session;
            this.message = message;
        }

        @Override
        protected void action() {
            try {
                doMessageHandle();
            } catch (MessageException e) {
                LogUtil.error(e, "执行消息处理逻辑中断, message:" + message);
                RespMessage resp = new RespMessage();
                resp.setId(message.getId() + 1);
                resp.setCode(e.getGameCode().getCode());
                resp.setParams(e.getMessage());
                session.sendMessage(resp);
            } catch (Throwable t) {
                LogUtil.error(t, "执行消息处理逻辑出错, message:" + message);
                RespMessage resp = new RespMessage();
                resp.setId(message.getId() + 1);
                resp.setCode(GameCodeEnum.UNKNOWN_ERROR.getCode());
                resp.setParams("未知错误");
                session.sendMessage(resp);
            }
        }

        private void doMessageHandle() {
            Object data = message.getData();
            if (data instanceof ChatLoginReq || data instanceof GetNameReq) {
                if (session.getSubject() == null) {
                    GameSubject subject = new Player();
                    session.setSubject(subject);
                    subject.setSession(session);
                }
            } else if (session.getSubject() == null && !MessageHandlerHolder.allowGuestRequest(message.getId())) {
                throw new MessageException(GameCodeEnum.MESSAGE_ILLEGAL, "player.not.login", "用户未登录，不能执行该操作");
            }

            IMessageHandler<Object> handler = MessageHandlerHolder.getSingletonHandler(message.getId());
            if (handler == null) {
                throw new MessageException(GameCodeEnum.NO_HANDLER, "handler.not.found", "未知消息id");
            }

            if (handler instanceof IHttpMessageHandler) {
                RespMessage resp = ((IHttpMessageHandler) handler).call(message, session.getSubject());
                if (resp != null) {
                    session.sendMessage(resp);
                }
            } else {
                handler.action(message, session.getSubject());
            }
        }
    }
}
