package cn.laoshini.game.example.tank.constant;

/**
 * 禁止登录类型枚举
 *
 * @author fagarine
 */
public enum ForbidType {

    /**
     * 非禁止状态
     */
    NONE(0, 0),
    /**
     * 强制下线
     */
    OFFLINE(1, 0),
    /**
     * 禁登录5分钟
     */
    FIVE_MIN(2, 5 * 60),
    /**
     * 禁登录1小时
     */
    ONE_HOUR(3, 60 * 60),
    /**
     * 禁登录6小时
     */
    SIX_HOUR(4, 6 * 60 * 60),
    /**
     * 禁登录1天
     */
    ONE_DAY(5, 24 * 60 * 60),
    /**
     * 禁登录3天
     */
    THREE_DAY(6, 3 * 24 * 60 * 60),
    /**
     * 禁登录7天
     */
    ONE_WEEK(7, 7 * 24 * 60 * 60),
    /**
     * 禁登录1月
     */
    ONE_MONTH(8, 30 * 24 * 60 * 60),
    /**
     * 永久禁止
     */
    FOREVER(66, -1),
    ;

    private int code;

    /**
     * 对应禁止登录时长，单位：秒
     */
    private int duration;

    ForbidType(int code, int duration) {
        this.code = code;
        this.duration = duration;
    }

    public int getCode() {
        return code;
    }

    public int getDuration() {
        return duration;
    }
}
