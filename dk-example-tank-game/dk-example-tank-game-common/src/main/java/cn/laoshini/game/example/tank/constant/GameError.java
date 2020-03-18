package cn.laoshini.game.example.tank.constant;

/**
 * @author fagarine
 */
public enum GameError {

    /**
     * 禁止登录
     */
    LOGIN_FORBID(102001, "禁止登录"),
    /**
     * 非法请求
     */
    ILLEGAL_REQUEST(102002, "非法请求"),
    /**
     * 角色锁定中
     */
    ROLE_LOCKED(102003, "角色状态异常"),
    /**
     * 玩家昵称重复
     */
    SAME_NICK(102004, "玩家昵称已被使用"),
    /**
     * 未找到房间
     */
    ROOM_NOT_FOUND(102011, "未找到房间"),
    /**
     * 玩家已在房间中
     */
    ALREADY_IN_ROOM(102012, "玩家已在房间中"),
    /**
     * 玩家未在房间中
     */
    NOT_IN_ROOM(102013, "玩家未在房间中"),
    /**
     * 房间不存在
     */
    ROOM_NOT_EXISTS(102014, "房间不存在"),
    /**
     * 玩家已死亡
     */
    PLAYER_DEAD(102015, "玩家已死亡"),
    /**
     * 不可信的坐标
     */
    INVALID_POS(102016, "参数错误"),
    /**
     * 子弹id无此配置
     */
    BULLET_NO_CONFIG(102017, "子弹id无此配置"),
    /**
     * 射击CD中
     */
    SHOT_CD(102018, "冷却中"),

    ;

    private int code;

    private String desc;

    GameError(int code) {
        this.code = code;
    }

    GameError(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
