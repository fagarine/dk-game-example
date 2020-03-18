package cn.laoshini.game.example.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.util.StringUtils;

import cn.laoshini.dk.client.AbstractNettyTcpClient;
import cn.laoshini.dk.client.JsonNettyTcpClient;
import cn.laoshini.dk.domain.msg.AbstractMessage;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.net.session.NettySession;
import cn.laoshini.game.example.chat.constant.ChatConstants;
import cn.laoshini.game.example.chat.message.ChatLoginReq;
import cn.laoshini.game.example.chat.message.PublicChatReq;

/**
 * @author fagarine
 */
public class ChatGameClientMain {

    public static void main(String[] args) throws InterruptedException {
        AbstractNettyTcpClient<NettySession, AbstractMessage<?>> client = new JsonNettyTcpClient<NettySession, AbstractMessage<?>>()
                .setServerHost("localhost").setServerPort(ChatConstants.SERVER_PORT)
                .setMessageSender(NettySession::sendMessage).setMessageDispatcher((session, message) -> {

                });

        Thread thread = new Thread(client);

        thread.start();

        while (client.isConnecting()) {
            Thread.sleep(100L);
        }

        if (!client.isConnected()) {
            System.out.println("连接服务器失败");
            return;
        }

        ReqMessage<ChatLoginReq> login = new ReqMessage<>();
        login.setId(ChatLoginReq.getId());
        login.setData(new ChatLoginReq());
        login.getData().setName("name");
        client.sendMsgToServer(login);

        System.out.println("成功开启聊天客户端，当前你在公共聊天频道，请输入聊天内容，按回车键发送：");
        while (true) {
            try {
                InputStreamReader in = new InputStreamReader(System.in);
                BufferedReader reader = new BufferedReader(in);
                String line = reader.readLine();
                if (!StringUtils.isEmpty(line)) {
                    System.out.println("玩家输入:" + line);
                    if ("exit".equalsIgnoreCase(line)) {
                        System.out.println("退出聊天客户端");
                        System.exit(0);
                    }

                    if (!client.isConnected()) {
                        System.out.println("服务器连接已断开");
                        System.exit(0);
                    }
                    ReqMessage<PublicChatReq> req = new ReqMessage<>();
                    req.setId(PublicChatReq.getId());
                    req.setData(new PublicChatReq());
                    req.getData().setContent(line);

                    client.sendMsgToServer(req);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}
