package cn.laoshini.game.example.tank.util;

/**
 * @author fagarine
 */
public class TimeHelper {
    private TimeHelper() {
    }

    private static final int SECOND_MS = 1000;

    public static int getCurrentSecond() {
        return (int) (System.currentTimeMillis() / SECOND_MS);
    }

    public static int millsToSecond(long mills) {
        return (int) (mills / SECOND_MS);
    }
}
