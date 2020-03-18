package cn.laoshini.game.example.tank.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author fagarine
 */
@Getter
@Setter
@ToString
@Configuration
@RefreshScope
public class TankConfig {

    /**
     * 游戏绑定端口
     */
    @Value("${tank.game.port}")
    private int port;

    /**
     * 消息接收处理线程池的核心线程数，默认为2
     */
    @Value("${tank.receive.thread:2}")
    private int receiveMessageThreads;
}
