package cn.laoshini.game.example.chat.constant;

/**
 * @author fagarine
 */
public class ChatConstants {
    private ChatConstants() {
    }

    public static final int GAME_ID = 101;

    public static final String GAME_NAME = "聊天游戏";

    public static final int SERVER_PORT = 9421;

    public static final int GET_NAME_REQ_ID = 1;

    public static final int LOGIN_REQ_ID = 3;

    public static final int PRIVATE_CHAT_REQ_ID = 5;

    public static final int PUBLIC_CHAT_REQ_ID = 7;

    public static final int TEXT_SHOW_LINES = 18;

    public static final int MESSAGE_HEAD = GAME_ID * 1000;

    public static final int PUSH_HEAD = (ChatConstants.GAME_ID * 10 + 1) * 100;

    public static final int GET_NAME_REQ_MESSAGE_ID = MESSAGE_HEAD + GET_NAME_REQ_ID;
    public static final int GET_NAME_RES_MESSAGE_ID = GET_NAME_REQ_MESSAGE_ID + 1;
    public static final int LOGIN_REQ_MESSAGE_ID = MESSAGE_HEAD + LOGIN_REQ_ID;
    public static final int LOGIN_RES_MESSAGE_ID = LOGIN_REQ_MESSAGE_ID + 1;
    public static final int PRIVATE_CHAT_REQ_MESSAGE_ID = MESSAGE_HEAD + PRIVATE_CHAT_REQ_ID;
    public static final int PRIVATE_CHAT_RES_MESSAGE_ID = PRIVATE_CHAT_REQ_MESSAGE_ID + 1;
    public static final int PUBLIC_CHAT_REQ_MESSAGE_ID = MESSAGE_HEAD + PUBLIC_CHAT_REQ_ID;
    public static final int PUBLIC_CHAT_RES_MESSAGE_ID = PUBLIC_CHAT_REQ_MESSAGE_ID + 1;
    public static final int PRIVATE_CHAT_PUSH_MESSAGE_ID = PUSH_HEAD + PRIVATE_CHAT_REQ_ID + 1;
    public static final int PUBLIC_CHAT_PUSH_MESSAGE_ID = PUSH_HEAD + PUBLIC_CHAT_REQ_ID + 1;

}
