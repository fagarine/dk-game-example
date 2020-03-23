package cn.laoshini.game.example.chat;

import cn.laoshini.dk.domain.msg.IMessage;
import cn.laoshini.dk.register.ClassIdReader;
import cn.laoshini.dk.register.ClassScanners;
import cn.laoshini.dk.register.MessageRegisterAdapter;

/**
 * 聊天消息类注册器
 * <p>
 * 像本示例项目这样完全符合当康系统默认消息格式规范的项目，通过包路径就可以完成注册，这里是为了演示如何使用自定义注册类实现消息类注册
 *
 * @author fagarine
 */
public class ChatMessageRegister extends MessageRegisterAdapter {

    public ChatMessageRegister() {
        setScanner(ClassScanners.newPackageScanner(new String[] { "cn.laoshini.game.example.chat.message" }));
        setIdReader(ClassIdReader.fieldReader(IMessage.ID_FIELD));
    }

}
