package cn.laoshini.game.example.chat.message;

import cn.laoshini.game.example.chat.constant.ChatConstants;

/**
 * @author fagarine
 */
public class PrivateChatPush {

    /** 使用常量记录id，可通过{@link cn.laoshini.dk.register.ClassIdReader#fieldReader(String)}创建一个id读取器对象读取类的id */
    public static final int MESSAGE_ID = (ChatConstants.GAME_ID * 10 + 1) * 100 + ChatConstants.PRIVATE_CHAT_REQ_ID + 1;

    private String name;

    private String content;

    /** 使用静态方法返回id，可通过{@link cn.laoshini.dk.register.ClassIdReader#methodReader(String)}创建一个id读取器对象读取类的id */
    public static int getId() {
        return MESSAGE_ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "PrivateChatPush{" + "name='" + name + '\'' + ", content='" + content + '\'' + '}';
    }
}
