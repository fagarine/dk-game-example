package cn.laoshini.game.example.chat.server.manager;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Lists;
import com.google.protobuf.Message;

import cn.laoshini.dk.domain.msg.RespMessage;
import cn.laoshini.dk.net.session.AbstractSession;

/**
 * @author fagarine
 */
public enum SessionHolder {
    /**
     * 使用枚举实现单例
     */
    INSTANCE;

    private static final Map<Long, String> ID_TO_NAME = new ConcurrentHashMap<>();

    private static final Map<String, AbstractSession> NAME_TO_SESSIONS = new ConcurrentHashMap<>();

    public static void register(String name, AbstractSession session) {
        ID_TO_NAME.put(session.getId(), name);
        NAME_TO_SESSIONS.put(name, session);
    }

    public static AbstractSession getSessionByName(String name) {
        return NAME_TO_SESSIONS.get(name);
    }

    public static boolean contains(String name) {
        return NAME_TO_SESSIONS.containsKey(name);
    }

    public static AbstractSession getSessionById(long id) {
        String name = ID_TO_NAME.get(id);
        return name == null ? null : NAME_TO_SESSIONS.get(name);
    }

    public static String getNameById(long id) {
        return ID_TO_NAME.get(id);
    }

    public static void pushToAll(RespMessage<?> message) {
        for (AbstractSession session : NAME_TO_SESSIONS.values()) {
            session.sendMessage(message);
        }
    }

    public static void pushToAll(RespMessage<?> message, Long... excludes) {
        List<Long> excludeIds = excludes == null ? null : Lists.newArrayList(excludes);
        pushToAll(message, excludeIds);
    }

    public static void pushToAll(Object message, Collection<Long> excludes) {
        for (AbstractSession session : NAME_TO_SESSIONS.values()) {
            if (excludes != null && excludes.contains(session.getId())) {
                continue;
            }

            session.sendMessage(message);
        }
    }

    public static void pushToAll(Message message, Long... excludes) {
        List<Long> excludeIds = excludes == null ? null : Lists.newArrayList(excludes);
        pushToAll(message, excludeIds);
    }
}
