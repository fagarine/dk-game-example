package cn.laoshni.game.example.chat.server;

import com.google.protobuf.Message;

import cn.laoshini.dk.net.msg.BaseProtobufMessage;
import cn.laoshini.dk.util.ByteMessageUtil;

/**
 * @author fagarine
 */
public class ProtobufUtil {
    private ProtobufUtil() {
    }

    public static final String TYPE_URL_PREFIX = "chat.example.laoshini.cn";

    public static BaseProtobufMessage.Base buildBase(int messageId, Message detail) {
        return ByteMessageUtil.buildBase(messageId, detail, TYPE_URL_PREFIX);
    }
}
