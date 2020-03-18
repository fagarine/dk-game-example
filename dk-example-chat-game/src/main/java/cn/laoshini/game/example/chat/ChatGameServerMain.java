package cn.laoshini.game.example.chat;

import cn.laoshini.dk.constant.GameServerProtocolEnum;
import cn.laoshini.dk.constant.MessageFormatEnum;
import cn.laoshini.dk.domain.GameServerConfig;
import cn.laoshini.dk.domain.msg.AbstractMessage;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.net.codec.JsonNettyMessageDecoder;
import cn.laoshini.dk.net.codec.JsonNettyMessageEncoder;
import cn.laoshini.dk.net.session.AbstractSession;
import cn.laoshini.dk.register.GameServerRegisterAdaptor;
import cn.laoshini.dk.register.IGameServerRegister;
import cn.laoshini.dk.register.IMessageHandlerRegister;
import cn.laoshini.dk.register.IMessageRegister;
import cn.laoshini.dk.register.Registers;
import cn.laoshini.dk.server.AbstractGameServer;
import cn.laoshini.dk.server.NettyServerFactory;
import cn.laoshini.dk.starter.DangKangGameStarter;
import cn.laoshini.dk.util.LogUtil;
import cn.laoshini.game.example.chat.constant.ChatConstants;
import cn.laoshini.game.example.chat.server.manager.ReceivedMessageQueue;

/**
 * 服务端程序启动类
 *
 * @author fagarine
 */
public class ChatGameServerMain {

    public static void main(String[] args) {
        // 游戏服务器快速启动方式1：通过各类游戏功能注册器，启动当康系统容器后立即启动；该方式启动的服务器可以实现更高的定制操作
        //        DangKangGameStarter.get().packagePrefixes("cn.laoshini.game").gameServer(chatGameServerRegister())
        //                .message(chatMessageRegister()).messageHandler(chatMessageHandlerRegister()).start();

        // 游戏服务器快速启动方式2：启动当康系统容器后，通过GameServerConfig配置创建一个服务器线程对象（该方法目前只支持TCP服务器）；
        // 这种方式的优点是简单、快速的启动服务器，不需要了解太多，同时这样启动的服务器在消息交互相关的功能由系统写死，无法修改
        DangKangGameStarter.get().packagePrefixes("cn.laoshini.game.example.chat").message(chatMessageRegister())
                .start();
        GameServerConfig config = GameServerConfig.builder().gameId(ChatConstants.GAME_ID)
                .gameName(ChatConstants.GAME_NAME).serverId(1001).serverName("聊天服务器1").port(ChatConstants.SERVER_PORT)
                .protocol(GameServerProtocolEnum.TCP).idleTime(300).format(MessageFormatEnum.JSON).build();
        AbstractGameServer server = NettyServerFactory.buildTcpServerByGameConfig(config);
        server.start();
    }

    private static IMessageRegister chatMessageRegister() {
        return Registers.newFieldIdMessageRegister(new String[] { "cn.laoshini.game.example.chat.message" });
    }

    private static IMessageHandlerRegister chatMessageHandlerRegister() {
        // 像这样直接使用当康系统自带的注册器对象的，当康系统容器启动时也会自动添加上，可以不手动添加，但必须填写项目的包路径前缀
        return Registers.dangKangMessageHandlerRegister();
    }

    private static IGameServerRegister<AbstractSession, AbstractMessage<?>> chatGameServerRegister() {
        GameServerRegisterAdaptor<AbstractSession, AbstractMessage<?>> register = Registers.newTcpGameServerRegister();
        register.setPort(ChatConstants.SERVER_PORT).setGameName(ChatConstants.GAME_NAME).setTcpNoDelay()
                // 消息解码器
                .setMessageDecode(new JsonNettyMessageDecoder())
                // 消息编码前
                .setMessageEncode(new JsonNettyMessageEncoder())
                // 连接建立成功时的逻辑
                .onConnected(session -> LogUtil.debug("new connection:" + session.getId()))
                // 连接异常时的逻辑
                .onConnectException((session, cause) -> LogUtil.error("connect exception"))
                // 消息发送逻辑
                .onMessageSend(AbstractSession::sendMessage)
                // 消息到达处理逻辑
                .onMessageDispatcher(
                        (session, message) -> ReceivedMessageQueue.addMessage(session, (ReqMessage) message));
        return register;
    }
}
