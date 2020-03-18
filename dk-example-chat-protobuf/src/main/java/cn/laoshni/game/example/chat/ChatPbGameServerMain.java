package cn.laoshni.game.example.chat;

import cn.laoshini.dk.constant.GameServerProtocolEnum;
import cn.laoshini.dk.constant.MessageFormatEnum;
import cn.laoshini.dk.domain.GameServerConfig;
import cn.laoshini.dk.register.IMessageHandlerRegister;
import cn.laoshini.dk.register.Registers;
import cn.laoshini.dk.server.AbstractNettyTcpGameServer;
import cn.laoshini.dk.server.NettyServerFactory;
import cn.laoshini.dk.starter.DangKangGameStarter;
import cn.laoshini.game.example.chat.constant.ChatConstants;

/**
 * 服务端程序启动类
 *
 * @author fagarine
 */
public class ChatPbGameServerMain {

    public static void main(String[] args) {
        // 使用protobuf通信
        DangKangGameStarter.get().messageHandler(chatMessageHandlerRegister()).start();
        GameServerConfig config = GameServerConfig.builder().gameId(ChatConstants.GAME_ID)
                .gameName(ChatConstants.GAME_NAME).serverId(1002).serverName("聊天服务器2").port(ChatConstants.SERVER_PORT)
                .protocol(GameServerProtocolEnum.TCP).idleTime(300).format(MessageFormatEnum.PROTOBUF).build();
        AbstractNettyTcpGameServer server = NettyServerFactory.buildTcpServerByGameConfig(config);
        server.start();
    }

    private static IMessageHandlerRegister chatMessageHandlerRegister() {
        // 像这样直接使用当康系统自带的注册器对象的，当康系统容器启动时也会自动添加上，可以不手动添加，但必须填写项目的类扫描路径
        return Registers.newDangKangHandlerRegister(new String[] { "cn.laoshni.game.example.chat.server.protobuf" });
    }
}
