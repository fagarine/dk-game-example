package cn.laoshini.game.example.tank.constant;

/**
 * 房间类型枚举
 *
 * @author fagarine
 */
public enum RoomType {

    /**
     * 普通房间
     */
    NORMAL(1, 20),

    /**
     * 中等房间
     */
    MIDDLE(2, 50),

    /**
     * 私密房间（小型）
     */
    SECRET(3, 20, true),

    /**
     * 私密房间（大型）
     */
    SECRET_BIG(4, 50, true),

    /**
     * 大房间
     */
    BIG(10, 100),
    ;

    private int type;

    private int capacity;

    private boolean secret = false;

    RoomType(int type, int capacity) {
        this.type = type;
        this.capacity = capacity;
    }

    RoomType(int type, int capacity, boolean secret) {
        this.type = type;
        this.capacity = capacity;
        this.secret = secret;
    }

    public int getType() {
        return type;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isSecret() {
        return secret;
    }
}
