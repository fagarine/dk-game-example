package cn.laoshini.game.example.tank.executor;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import cn.laoshini.dk.util.LogUtil;

/**
 * @author fagarine
 */
public class TankScheduledExecutor extends ScheduledThreadPoolExecutor {

    public TankScheduledExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);

        if (t != null) {
            LogUtil.error("定时任务执行出错:" + r.getClass().getSimpleName(), t);
        }
    }
}
