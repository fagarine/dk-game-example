package cn.laoshni.game.example.chat.client;

import cn.laoshni.game.example.chat.server.ProtobufUtil;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import javafx.stage.Stage;

import cn.laoshini.dk.client.ProtobufNettyTcpClient;
import cn.laoshini.dk.constant.GameCodeEnum;
import cn.laoshini.dk.net.msg.BaseProtobufMessage;
import cn.laoshini.dk.net.session.NettySession;
import cn.laoshini.game.example.chat.client.ChatClientView;
import cn.laoshini.game.example.chat.constant.ChatConstants;
import cn.laoshini.game.example.chat.pb.ChatGameMessages;

/**
 * 聊天客户端程序交互界面，使用Protobuf通信
 *
 * @author fagarine
 */
public class ChatPbClientView extends ChatClientView {

    public ChatPbClientView(String name, Stage primaryStage) {
        super(name, primaryStage);
    }

    @Override
    protected void createClient() {
        client = new ProtobufNettyTcpClient<NettySession, BaseProtobufMessage.Base>().setServerHost("localhost")
                .setServerPort(ChatConstants.SERVER_PORT).setMessageSender(NettySession::sendMessage)
                .setMessageDispatcher((session, message) -> {
                    int messageId = message.getMessageId();
                    if (GameCodeEnum.OK.getCode() != message.getCode()) {
                        addErrorShowText(message.getParams());
                        if (messageId == ChatConstants.GET_NAME_RES_MESSAGE_ID
                            || messageId == ChatConstants.LOGIN_RES_MESSAGE_ID) {
                            addErrorShowText("玩家登录失败");
                        }
                        return;
                    }

                    StringBuilder text = new StringBuilder();
                    Any detail = message.getDetail();
                    switch (messageId) {
                        case ChatConstants.GET_NAME_RES_MESSAGE_ID:
                            ChatGameMessages.GetNameRes res = unpack(detail, ChatGameMessages.GetNameRes.class);
                            name = res.getName();
                            return;
                        case ChatConstants.LOGIN_RES_MESSAGE_ID:
                            text.append("登录成功，玩家[").append(name).append("]，欢迎你来到").append(ChatConstants.GAME_NAME)
                                    .append("！");
                            break;
                        case ChatConstants.PRIVATE_CHAT_RES_MESSAGE_ID:
                            ChatGameMessages.PrivateChatRes privateRes = unpack(detail,
                                    ChatGameMessages.PrivateChatRes.class);
                            text.append("我 : ").append(privateRes.getContent());
                            break;
                        case ChatConstants.PUBLIC_CHAT_RES_MESSAGE_ID:
                            ChatGameMessages.PublicChatRes publicRes = unpack(detail,
                                    ChatGameMessages.PublicChatRes.class);
                            text.append("我 : ").append(publicRes.getContent());
                            break;
                        case ChatConstants.PRIVATE_CHAT_PUSH_MESSAGE_ID:
                        case ChatConstants.PUBLIC_CHAT_PUSH_MESSAGE_ID:
                            ChatGameMessages.ChatPush push = unpack(detail, ChatGameMessages.ChatPush.class);
                            text.append(push.getName()).append(" : ").append(push.getContent());
                            break;
                        default:
                            return;
                    }

                    addShowTextContent(text.toString());
                });
    }

    private <T extends com.google.protobuf.Message> T unpack(Any msg, Class<T> type) {
        try {
            return msg.unpack(type);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendMsg(int id, com.google.protobuf.Message detail) {
        client.sendMsgToServer(ProtobufUtil.buildBase(id, detail));
    }

    @Override
    protected void sendGetNameMsg() {
        sendMsg(ChatConstants.GET_NAME_REQ_MESSAGE_ID, ChatGameMessages.GetNameReq.newBuilder().build());
    }

    @Override
    protected void sendClientLoginMsg() {
        sendMsg(ChatConstants.LOGIN_REQ_MESSAGE_ID, ChatGameMessages.ChatLoginReq.newBuilder().setName(name).build());
    }

    @Override
    protected void sendPublicChatMsg(String text) {
        sendMsg(ChatConstants.PUBLIC_CHAT_REQ_MESSAGE_ID,
                ChatGameMessages.PublicChatReq.newBuilder().setContent(text).build());
    }

    @Override
    protected void sendPrivateChatMsg(String target, String text) {
        sendMsg(ChatConstants.PRIVATE_CHAT_REQ_MESSAGE_ID,
                ChatGameMessages.PrivateChatReq.newBuilder().setName(target).setContent(text).build());
    }
}
