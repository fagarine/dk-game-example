package cn.laoshini.game.example.tank.util;

import org.apache.commons.lang3.RandomUtils;

/**
 * @author fagarine
 */
public class RandomHelper {
    private RandomHelper() {
    }

    public static boolean isHitRangeIn100(final int prob) {
        final int seed = randomInSize(100);
        boolean bool = false;
        if (seed < prob) {
            bool = true;
        }
        return bool;
    }

    public static boolean isHitRangeIn1000(final int prob) {
        final int seed = randomInSize(1000);
        boolean bool = false;
        if (seed < prob) {
            bool = true;
        }
        return bool;
    }

    public static boolean isHitRangeIn100000(final int prob) {
        final int seed = randomInSize(100000);
        boolean bool = false;
        if (seed < prob) {
            bool = true;
        }
        return bool;
    }

    public static int randomInSize(final int size) {
        return RandomUtils.nextInt(0, size);
    }

    public static long randomInSize(final long size) {
        return RandomUtils.nextLong(0, size);
    }
}
