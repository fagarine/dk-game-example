package cn.laoshini.game.example.tank.domain;

import lombok.Getter;
import lombok.Setter;

import cn.laoshini.dk.domain.GameSubject;
import cn.laoshini.dk.domain.msg.RespMessage;
import cn.laoshini.dk.util.LogUtil;
import cn.laoshini.game.example.tank.constant.TankConstants;
import cn.laoshini.game.example.tank.entity.Account;
import cn.laoshini.game.example.tank.entity.Role;
import cn.laoshini.game.example.tank.util.PositionUtil;

/**
 * @author fagarine
 */
@Getter
@Setter
public class TankPlayer extends GameSubject {

    private transient Account account;

    private transient Role role;

    private boolean online;

    /**
     * 玩家所在房间号
     */
    private int roomId;
    /**
     * 玩家坐标
     */
    protected Position pos;
    /**
     * 玩家朝向
     */
    protected int direction;

    protected int tankId;

    protected int speed;

    protected boolean alive;

    private long nextMoveTime;

    protected long nextShotTime;

    protected MovePath move = new MovePath();

    private boolean robot;

    public int getAccountId() {
        return account.getAccountId();
    }

    public long getRoleId() {
        return role == null ? -1 : role.getRoleId();
    }

    public String getNick() {
        return role.getNick();
    }

    public int getPortrait() {
        return role.getPortrait();
    }

    @Override
    public int getGameId() {
        return TankConstants.GAME_ID;
    }

    @Override
    public int getServerId() {
        return account.getServerId();
    }

    public void copyTo(TankPlayer player, boolean reconnect) {
        player.setAccount(account);
        player.setRole(role);

        if (reconnect) {
            player.setOnline(online);
            player.setRoomId(roomId);
            player.setPos(pos);
            player.setDirection(direction);
            player.setTankId(tankId);
            player.setAlive(alive);
        }
    }

    public int logout() {
        this.online = false;
        this.setSession(null);
        int roomId = this.roomId;
        this.roomId = 0;
        this.pos = null;
        this.direction = 0;
        this.tankId = 0;
        this.alive = false;
        return roomId;
    }

    /**
     * 开始移动
     *
     * @param target 目标坐标
     */
    public void beginMove(Position target) {
        move.setSource(new Position(pos));
        move.setTarget(target);
        move.setSpeed(speed);
        move.setMoveEnd(false);
        move.setBeginTime(System.currentTimeMillis());
    }

    /**
     * 更新当前坐标
     */
    public void updatePos() {
        if (move.isMoveEnd()) {
            return;
        }

        long now = System.currentTimeMillis();
        int time = (int) (now - move.getBeginTime());

        Position source = move.getSource();
        Position target = move.getTarget();
        double len = PositionUtil.distance(source, target);
        int moved = speed * time / 1000;
        if (moved >= len) {
            moveEnd(target);
        } else {
            double ratio = moved / len;
            pos.setX((int) (source.getX() + (target.getX() - source.getX()) * ratio));
            pos.setY((int) (source.getY() + (target.getY() - source.getY()) * ratio));
        }
    }

    public void moveEnd(Position target) {
        if (move.isMoveEnd()) {
            return;
        }
        move.setMoveEnd(true);
        if (target != null) {
            pos.toPosition(target);
        }
    }

    /**
     * 发送返回消息
     *
     * @param resp 消息对象
     */
    public void sendRespMessage(RespMessage<?> resp) {
        if (robot) {
            return;
        }
        getSession().sendMessage(resp);
        LogUtil.s2cMessage("发送消息: {}", resp);
    }

    /**
     * 将传入数据拼装成一个{@link RespMessage}消息对象发送
     *
     * @param messageId 消息id
     * @param data 消息内容
     */
    public void sendRespMessage(int messageId, Object data) {
        if (robot) {
            return;
        }
        RespMessage<Object> rs = new RespMessage<>();
        rs.setId(messageId);
        rs.setData(data);
        sendRespMessage(rs);
    }

    /**
     * 将传入数据拼装成一个返回错误提示的{@link RespMessage}消息对象发送
     *
     * @param messageId 消息id
     * @param errorCode 错误码
     * @param errorMessage 提示信息
     */
    public void sendAsErrorRespMessage(int messageId, int errorCode, String errorMessage) {
        if (robot) {
            return;
        }
        RespMessage<Object> rs = new RespMessage<>();
        rs.setId(messageId);
        rs.setCode(errorCode);
        rs.setParams(errorMessage);
        sendRespMessage(rs);
    }

    @Override
    public int hashCode() {
        return account == null ? super.hashCode() : account.getAccountId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TankPlayer) {
            TankPlayer player = (TankPlayer) obj;
            if (role != null) {
                return player.getRole() != null && getRoleId() == player.getRoleId();
            } else if (player.getRole() == null) {
                return getAccountId() == player.getAccountId();
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "TankPlayer{roleId:" + getRoleId() + ", nick:" + getNick() + ", robot:" + robot + "}";
    }
}
