package cn.laoshini.game.example.tank.manage.util;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import cn.laoshini.dk.util.LogUtil;
import cn.laoshini.game.example.tank.domain.TankPlayer;
import cn.laoshini.game.example.tank.executor.TankScheduledExecutor;
import cn.laoshini.game.example.tank.manage.room.Room;
import cn.laoshini.game.example.tank.manage.worker.TankResurgenceWorker;

/**
 * 定时任务工具类，使用JDK自带的{@link ScheduledThreadPoolExecutor}简单实现
 *
 * @author fagarine
 */
public class TimerTaskUtil {
    private TimerTaskUtil() {
    }

    /**
     * 定时任务线程池核心线程数，目前项目中需要用到定时器的任务很少，且任务工作量较轻，暂设置为4
     */
    private static final int THREADS = 4;

    private static final ThreadFactory THREAD_FACTORY = new BasicThreadFactory.Builder()
            .namingPattern("tank-timer-pool-%d")
            .uncaughtExceptionHandler((t, e) -> LogUtil.error(String.format("定时任务[%s]执行出错", t.getName()), e)).build();

    /**
     * 定时任务线程池
     */
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR = new TankScheduledExecutor(THREADS,
            THREAD_FACTORY);

    /**
     * 立即执行任务（异步执行）
     *
     * @param task 任务对象
     */
    public static void execute(Runnable task) {
        SCHEDULED_EXECUTOR.execute(task);
    }

    /**
     * 提交只执行一次的任务
     *
     * @param task 任务
     * @param delay 延迟启动时长，单位：毫秒
     */
    public static void singleTask(Runnable task, long delay) {
        SCHEDULED_EXECUTOR.schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 提交按固定周期执行的任务（重复执行的任务）
     *
     * @param task 任务
     * @param initialDelay 初次启动延迟时长，单位：毫秒
     * @param period 每次任务启动间隔时长（周期），单位：毫秒
     */
    public static void fixedRateTask(Runnable task, long initialDelay, long period) {
        SCHEDULED_EXECUTOR.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * 提交按固定延迟时间执行的任务（重复执行的任务）
     *
     * @param task 任务
     * @param initialDelay 初次启动延迟时长，单位：毫秒
     * @param delay 下次任务启动距离上次任务结束的时长（延迟时间），单位：毫秒
     */
    public static void fixedDelayTask(Runnable task, long initialDelay, long delay) {
        SCHEDULED_EXECUTOR.scheduleWithFixedDelay(task, initialDelay, delay, TimeUnit.MILLISECONDS);
    }

    public static void addTankResurgenceTask(TankPlayer player, Room room, int delay) {
        singleTask(new TankResurgenceWorker(player, room), delay * 1000);
    }
}
