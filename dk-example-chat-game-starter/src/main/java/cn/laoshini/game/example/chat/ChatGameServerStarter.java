package cn.laoshini.game.example.chat;

import cn.laoshini.dk.starter.DangKangGameStarter;

/**
 * @author fagarine
 */
public class ChatGameServerStarter {

    public static void main(String[] args) {
        // 以下代码即可启动游戏服线程
        DangKangGameStarter.get().propertyConfigs("application.properties").start();

        // 该项目只是为了演示配置项自动启动游戏服的功能，没有单独实现客户端，使用dk-example-chat-game的客户端程序即可
    }
}
