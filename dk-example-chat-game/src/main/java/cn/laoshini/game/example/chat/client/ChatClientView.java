package cn.laoshini.game.example.chat.client;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import cn.laoshini.dk.client.AbstractNettyTcpClient;
import cn.laoshini.dk.client.JsonNettyTcpClient;
import cn.laoshini.dk.constant.Constants;
import cn.laoshini.dk.constant.GameCodeEnum;
import cn.laoshini.dk.domain.msg.AbstractMessage;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.domain.msg.RespMessage;
import cn.laoshini.dk.net.session.NettySession;
import cn.laoshini.dk.register.Registers;
import cn.laoshini.dk.util.LogUtil;
import cn.laoshini.dk.util.StringUtil;
import cn.laoshini.game.example.chat.constant.ChatConstants;
import cn.laoshini.game.example.chat.message.ChatLoginReq;
import cn.laoshini.game.example.chat.message.ChatLoginRes;
import cn.laoshini.game.example.chat.message.GetNameReq;
import cn.laoshini.game.example.chat.message.GetNameRes;
import cn.laoshini.game.example.chat.message.PrivateChatPush;
import cn.laoshini.game.example.chat.message.PrivateChatReq;
import cn.laoshini.game.example.chat.message.PrivateChatRes;
import cn.laoshini.game.example.chat.message.PublicChatPush;
import cn.laoshini.game.example.chat.message.PublicChatReq;
import cn.laoshini.game.example.chat.message.PublicChatRes;

/**
 * 聊天客户端程序交互界面
 *
 * @author fagarine
 */
public class ChatClientView<M> {

    protected String name;

    protected Stage primaryStage;

    protected AbstractNettyTcpClient client;

    protected Text showText;

    public ChatClientView(String name, Stage primaryStage) {
        this.name = name;
        this.primaryStage = primaryStage;
    }

    public void showWindow() {
        // 设置窗体标题
        primaryStage.setTitle(ChatConstants.GAME_NAME);
        BorderPane mainPane = new BorderPane();

        // 添加工作区
        addWorkPane(mainPane);

        Scene scene = new Scene(mainPane, 600, 420);
        scene.setFill(Color.OLDLACE);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.setOnCloseRequest(new WindowCloseEventHandler());
        primaryStage.show();

        startClient();

        if (StringUtil.isNotEmptyString(name)) {
            // 如果传入了玩家名称，直接登录
            clientLogin();
        } else {
            // 从服务端获取一个名称
            sendGetNameMsg();

            int tryNum = 0;
            while (tryNum++ <= 30) {
                try {
                    Thread.sleep(100);

                    // 如果从服务端获取名称成功，游客登录
                    if (StringUtil.isNotEmptyString(name)) {
                        clientLogin();
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void sendGetNameMsg() {
        ReqMessage<GetNameReq> login = new ReqMessage<>();
        login.setId(GetNameReq.getId());
        login.setData(new GetNameReq());
        client.sendMsgToServer(login);
    }

    /**
     * 启动一个Netty客户端
     */
    private void startClient() {
        createClient();

        Thread thread = new Thread(client);
        thread.start();

        int count = 0;
        while (client.isConnecting()) {
            try {
                if (count % 10 == 0) {
                    addShowTextContent("正在连接服务器...");
                }
                count++;
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (!client.isConnected()) {
            addErrorShowText("连接服务器失败");
        } else {
            addShowTextContent("已成功连接到服务器，开始登录服务器...");
        }
    }

    protected void createClient() {
        client = new JsonNettyTcpClient<NettySession, AbstractMessage<?>>().setServerHost("localhost")
                .setServerPort(ChatConstants.SERVER_PORT).setMessageRegister(
                        Registers.newFieldIdMessageRegister(new String[] { "cn.laoshini.game.example.chat.message" }))
                .setMessageSender(NettySession::sendMessage).setMessageDispatcher((session, message) -> {
                    if (message instanceof RespMessage) {
                        if (GameCodeEnum.OK.getCode() != message.getCode()) {
                            addErrorShowText(message.getParams());
                            if (message.getData() instanceof ChatLoginRes || message.getData() instanceof GetNameRes) {
                                addErrorShowText("玩家登录失败");
                            }
                            return;
                        }

                        StringBuilder text = new StringBuilder();
                        Object detail = message.getData();
                        if (detail instanceof ChatLoginRes) {
                            text.append("登录成功，玩家[").append(name).append("]，欢迎你来到").append(ChatConstants.GAME_NAME)
                                    .append("！");
                        } else if (detail instanceof GetNameRes) {
                            GetNameRes res = (GetNameRes) detail;
                            name = res.getName();
                            return;
                        } else if (detail instanceof PrivateChatRes) {
                            PrivateChatRes res = (PrivateChatRes) detail;
                            text.append("我 : ").append(res.getContent());
                        } else if (detail instanceof PublicChatRes) {
                            PublicChatRes res = (PublicChatRes) detail;
                            text.append("我 : ").append(res.getContent());
                        } else if (detail instanceof PrivateChatPush) {
                            PrivateChatPush push = (PrivateChatPush) detail;
                            text.append(push.getName()).append(" : ").append(push.getContent());
                        } else if (detail instanceof PublicChatPush) {
                            PublicChatPush push = (PublicChatPush) detail;
                            text.append(push.getName()).append(" : ").append(push.getContent());
                        }

                        addShowTextContent(text.toString());
                    }
                });
    }

    private void clientLogin() {
        // 设置窗体标题
        primaryStage.setTitle(ChatConstants.GAME_NAME + " | [当前玩家: " + name + "]");

        sendClientLoginMsg();
    }

    protected void sendClientLoginMsg() {
        ReqMessage<ChatLoginReq> login = new ReqMessage<>();
        login.setId(ChatLoginReq.getId());
        login.setData(new ChatLoginReq());
        login.getData().setName(name);
        client.sendMsgToServer(login);
    }

    protected void addErrorShowText(String content) {
        if (StringUtil.isEmptyString(content)) {
            return;
        }

        String text = "错误信息：" + content;
        addShowTextContent(text);
    }

    protected void addShowTextContent(String content) {
        if (StringUtil.isEmptyString(content)) {
            return;
        }

        String origin = showText.getText();
        int lines = origin.split(Constants.LINE_SEPARATOR).length;
        if (lines > ChatConstants.TEXT_SHOW_LINES) {
            origin = origin.substring(origin.indexOf(Constants.LINE_SEPARATOR) + 2);
        }

        StringBuilder text = new StringBuilder(origin);
        if (StringUtil.isNotEmptyString(origin)) {
            text.append(Constants.LINE_SEPARATOR);
        }

        text.append(content);
        showText.setText(text.toString());
    }

    /**
     * 添加主要工作区
     *
     * @param mainPane
     */
    private void addWorkPane(BorderPane mainPane) {
        AnchorPane anchorPane = loadMainView();
        mainPane.setCenter(anchorPane);
        anchorPane.setPrefWidth(mainPane.getPrefWidth());

        AnchorPane showPane = (AnchorPane) anchorPane.getChildren().get(1);
        showText = (Text) showPane.getChildren().get(0);

        // 发送公共聊天
        TextArea messageText = (TextArea) anchorPane.getChildren().get(3);
        messageText.setOnKeyReleased(event -> {
            if (KeyCode.ENTER.equals(event.getCode())) {
                sendPublicChat(messageText);
            }
        });
        Button publicBtn = (Button) anchorPane.getChildren().get(4);
        Tooltip publicToolTip = new Tooltip("输入框中回车可直接发送到公聊");
        publicBtn.setTooltip(publicToolTip);
        publicBtn.setOnAction(event -> sendPublicChat(messageText));

        // 发送私聊
        TextArea nameText = (TextArea) anchorPane.getChildren().get(6);
        nameText.setOnKeyReleased(event -> {
            if (KeyCode.ENTER.equals(event.getCode())) {
                sendPrivateChat(nameText, messageText);
            }
        });
        Button privateBtn = (Button) anchorPane.getChildren().get(7);
        Tooltip privateToolTip = new Tooltip("用户名输入框中回车可直接发送到私聊");
        privateBtn.setTooltip(privateToolTip);
        privateBtn.setOnAction(event -> sendPrivateChat(nameText, messageText));
    }

    private void sendPublicChat(TextArea messageText) {
        String text = messageText.getText().trim();
        if (StringUtil.isEmptyString(text)) {
            LogUtil.error("未输入聊天信息");
        } else {
            sendPublicChatMsg(text);
        }

        messageText.setText("");
        messageText.requestFocus();
    }

    protected void sendPublicChatMsg(String text) {
        ReqMessage<PublicChatReq> req = new ReqMessage<>();
        req.setId(PublicChatReq.getId());
        req.setData(new PublicChatReq());
        req.getData().setContent(text);
        client.sendMsgToServer(req);
    }

    private void sendPrivateChat(TextArea nameText, TextArea messageText) {
        String target = nameText.getText().trim();
        String text = messageText.getText().trim();
        if (StringUtil.isEmptyString(text)) {
            LogUtil.error("未输入聊天信息");
            messageText.setText("");
            messageText.requestFocus();
        } else if (StringUtil.isEmptyString(target)) {
            LogUtil.error("未输入用户信息");
            nameText.setText("");
            nameText.requestFocus();
        } else {
            // 发送消息
            sendPrivateChatMsg(target, text);

            nameText.setText("");
            messageText.setText("");
            messageText.requestFocus();
        }
    }

    protected void sendPrivateChatMsg(String target, String text) {
        ReqMessage<PrivateChatReq> req = new ReqMessage<>();
        req.setId(PrivateChatReq.getId());
        req.setData(new PrivateChatReq());
        req.getData().setName(target);
        req.getData().setContent(text);
        client.sendMsgToServer(req);
    }

    private AnchorPane loadMainView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ChatClientView.class.getResource("/views/chatMainView.fxml"));
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    class WindowCloseEventHandler implements EventHandler<WindowEvent> {

        @Override
        public void handle(WindowEvent event) {
            LogUtil.info("窗口关闭");

            System.exit(0);
        }

    }
}
