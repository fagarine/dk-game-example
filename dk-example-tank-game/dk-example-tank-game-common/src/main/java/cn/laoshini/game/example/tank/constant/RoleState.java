package cn.laoshini.game.example.tank.constant;

/**
 * 角色状态枚举
 *
 * @author fagarine
 */
public enum RoleState {

    /**
     * 角色未创建
     */
    UNCREATED(0),

    /**
     * 角色已创建，正常状态
     */
    NORMAL(1),

    /**
     * 锁定中
     */
    LOCKED(2),
    ;

    private int state;

    RoleState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
