package cn.laoshini.game.example.tank.common;

import io.netty.util.AttributeKey;

import cn.laoshini.game.example.tank.domain.TankPlayer;

/**
 * @author fagarine
 */
public class ChannelAttr {

    public static final AttributeKey<TankPlayer> PLAYER = AttributeKey.valueOf("TANK PLAYER");
    public static final AttributeKey<Long> ROLE_ID = AttributeKey.valueOf("ROLE ID");
    public static final AttributeKey<Long> ID = AttributeKey.valueOf("ID");
}
