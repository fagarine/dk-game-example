package cn.laoshini.game.example.tank.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 机器人功能相关配置
 *
 * @author fagarine
 */
@Getter
@Setter
@ToString
@Configuration
@RefreshScope
public class TankRobotConfig {

    /**
     * 机器人功能开关，默认为关闭
     */
    @Value("${tank.robot.open:false}")
    private boolean robotOpen;

    /**
     * 可生成机器人的最大数量，默认为100
     */
    @Value("${tank.robot.max:100}")
    private int robotMax;

    /**
     * 一个房间内玩家人数少于该值，可以触发添加机器人，默认为10
     */
    @Value("${tank.robot.trigger:10}")
    private int robotTrigger;

    /**
     * 一个房间内机器人最大同时存在数量，默认为10
     */
    @Value("${tank.robot.room:10}")
    private int robotRoomMax;

    /**
     * 机器人单次保持在空闲状态的最大时间，单位为毫秒，默认为5000
     */
    @Value("${tank.robot.idle:5000}")
    private int robotIdleMax;

    /**
     * 机器人可以发现敌人的最大半径，默认为1000
     */
    @Value("${tank.robot.look.radius:1000}")
    private int robotLookRadius;

    /**
     * 机器人攻击敌人的最大距离，默认为500
     */
    @Value("${tank.robot.attack.radius:500}")
    private int robotAttackRadius;
}
