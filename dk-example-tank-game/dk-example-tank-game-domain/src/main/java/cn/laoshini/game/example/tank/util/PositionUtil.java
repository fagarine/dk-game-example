package cn.laoshini.game.example.tank.util;

import org.apache.commons.lang3.RandomUtils;

import cn.laoshini.dk.util.AngleUtil;
import cn.laoshini.game.example.tank.domain.Position;

/**
 * @author fagarine
 */
public class PositionUtil {
    private PositionUtil() {
    }

    public static final double HALF_SQRT2 = Math.sqrt(2) / 2D;

    public static final double[] OCTAGON_X_RATIO = { 1, HALF_SQRT2, 0, -HALF_SQRT2, -1, -HALF_SQRT2, 0, HALF_SQRT2 };

    public static final double[] OCTAGON_Y_RATIO = { 0, -HALF_SQRT2, -1, -HALF_SQRT2, 0, HALF_SQRT2, 1, HALF_SQRT2 };

    /**
     * 计算两个坐标点之间的距离
     *
     * @param source 起点坐标
     * @param target 目标点坐标
     * @return 返回距离
     */
    public static double distance(Position source, Position target) {
        return Math.sqrt(square(target.getX() - source.getX()) + square(target.getY() - source.getY()));
    }

    /**
     * 计算从起点到目标点的朝向
     *
     * @param source 起点坐标
     * @param target 目标点坐标
     * @return 返回八角朝向[0, 8)
     */
    public static int toDirection(Position source, Position target) {
        // 由于AngleUtil使用的是平面直角坐标系，Y轴向上为加，而游戏中一般Y轴向上为减，所以两个坐标的Y坐标位移取反后再计算角度
        int angle = (int) AngleUtil.position2Angle(source.getX(), target.getY(), target.getX(), source.getY());
        // 以下计算结果接近于四舍五入
        return ((angle + 22) / 45) % 8;
    }

    /**
     * 计算目标坐标点是否包含在起点（圆心）坐标指定半径的圆内
     *
     * @param sourceX 起点横轴坐标
     * @param sourceY 起点纵轴坐标
     * @param targetX 目标点横轴坐标
     * @param targetY 目标点纵轴坐标
     * @param radius 圆半径
     * @return 返回计算结果
     */
    public static boolean isInCircle(int sourceX, int sourceY, int targetX, int targetY, int radius) {
        checkNotNegative(radius, "圆半径");
        return square(radius) >= square(targetX - sourceX) + square(targetY - sourceY);
    }

    /**
     * 计算目标坐标点是否包含在起点（圆心）坐标指定半径的圆内
     *
     * @param source 起点坐标
     * @param radius 圆半径
     * @param target 目标点坐标
     * @return 返回计算结果
     */
    public static boolean isInCircle(Position source, int radius, Position target) {
        checkNotNegative(radius, "圆半径");
        if (source.equals(target)) {
            return true;
        }
        return square(radius) >= square(target.getX() - source.getX()) + square(target.getY() - source.getY());
    }

    /**
     * 计算目标坐标点是否包含在起点（圆心）坐标指定半径的圆内
     *
     * @param source 起点坐标
     * @param radius 圆半径
     * @param target 目标点坐标
     * @param targetRadius 目标半径
     * @return 返回计算结果
     */
    public static boolean isInCircle(Position source, int radius, Position target, int targetRadius) {
        checkNotNegative(radius, "圆半径");
        checkNotNegative(targetRadius, "目标半径");
        int len = radius + targetRadius;
        return square(len) >= square(target.getX() - source.getX()) + square(target.getY() - source.getY());
    }

    public static boolean isInTank(Position tankPos, int direction, int halfWidth, int halfHeight, Position target) {
        Position source = toTargetPos(tankPos, halfWidth, direction + 4);
        return isInRect(source, directionToAngle(direction), halfHeight << 1, halfWidth << 1, target);
    }

    public static int directionToAngle(int direction) {
        return ((8 - direction) % 8) * 45;
    }

    public static boolean isInRectByDirection(Position source, int direction, int width, int length, Position target) {
        return isInRect(source, directionToAngle(direction), width, length, target);
    }

    public static boolean isInRect(Position source, int angle, int width, int length, Position target) {
        return AngleUtil.isInRect(source.getX(), source.getY(), target.getX(), target.getY(), angle, width, length);
    }

    public static Position randomPosition(int width, int height, int border) {
        int x = RandomUtils.nextInt(border, width);
        int y = RandomUtils.nextInt(border, height);
        return new Position(x, y);
    }

    public static Position randomPosition(Position source, int direction, int len, int width, int height, int border) {
        if (len == 0) {
            return new Position(source);
        }
        return toTargetPosition(source, direction, width, height, border, len);
    }

    public static Position toTargetPosition(Position source, int direction, int width, int height, int border,
            int len) {
        Position target = toTargetPos(source, len, direction);
        if (target.getX() < border) {
            target.setX(border);
        } else if (target.getY() < border) {
            target.setY(border);
        } else if (target.getX() > width - border) {
            target.setX(width - border);
        } else if (target.getY() > height - border) {
            target.setY(height - border);
        }
        return target;
    }

    /**
     * 根据传入的坐标点，长度和角度，计算目标点坐标
     *
     * @param source 起点坐标
     * @param len 长度
     * @param direction 朝向
     * @return 返回目标点坐标
     */
    public static Position toTargetPos(Position source, int len, int direction) {
        if (len == 0) {
            return new Position(source);
        }
        int x = source.getX() + (int) (xOctagonRatio(direction) * len);
        int y = source.getY() + (int) (yOctagonRatio(direction) * len);
        return new Position(x, y);
    }

    public static double xOctagonRatio(int direction) {
        return OCTAGON_X_RATIO[direction % 8];
    }

    public static double yOctagonRatio(int direction) {
        return OCTAGON_Y_RATIO[direction % 8];
    }

    private static void checkNotNegative(int num, String errorMessage) {
        if (num < 0) {
            throw new IllegalArgumentException("[" + errorMessage + "]不能小于0");
        }
    }

    /**
     * 求平方
     *
     * @param num 数值
     * @return 返回传入数值的平方值
     */
    public static int square(int num) {
        return num * num;
    }
}
