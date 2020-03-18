package cn.laoshini.game.example.tank.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 白名单用户类型
 *
 * @author fagarine
 */
public enum WhiteNameType {

    /**
     * 不是白名单类型
     */
    NONE(0),

    /**
     * 测试
     */
    TEST(1),

    /**
     * GM
     */
    GM(2),

    ;

    private int code;

    private static final Map<Integer, WhiteNameType> CODE_TO_TYPES = new HashMap<>();

    WhiteNameType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static WhiteNameType getByCode(int code) {
        return CODE_TO_TYPES.get(code);
    }
}
