package cn.laoshini.game.example.tank.message.room;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import cn.laoshini.game.example.tank.annotation.TankMessage;
import cn.laoshini.game.example.tank.constant.TankConstants;
import cn.laoshini.game.example.tank.domain.Position;

/**
 * @author fagarine
 */
@Getter
@Setter
@Builder
@ToString
@TankMessage(id = BulletBlastPush.MESSAGE_ID)
public class BulletBlastPush {
    public static final int MESSAGE_ID = TankConstants.PUSH_HEAD + TankConstants.BULLET_BLAST_PUSH;

    /**
     * 子弹移动轨迹id
     */
    private Integer bid;

    /**
     * 子弹出现的坐标
     */
    private Position pos;

}
