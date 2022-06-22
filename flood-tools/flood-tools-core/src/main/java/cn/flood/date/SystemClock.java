package cn.flood.date;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SystemClock {

    private final long period;
    private volatile long now;

    private SystemClock(long period) {
        this.period = period;
        this.now = System.currentTimeMillis();
        this.scheduleClockUpdating();
    }

    private void scheduleClockUpdating() {
        ScheduledExecutorService scheduler =new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("scheduler-pool-%d").daemon(true).build());
        scheduler.scheduleAtFixedRate(() -> {
            this.now = System.currentTimeMillis();
        }, this.period, this.period, TimeUnit.MILLISECONDS);
    }

    private long currentTimeMillis() {
        return this.now;
    }

    private static SystemClock instance() {
        return SystemClock.InstanceHolder.INSTANCE;
    }

    public static long now() {
        return instance().currentTimeMillis();
    }

    public static String nowDate() {
        return (new Timestamp(instance().currentTimeMillis())).toString();
    }

    private static class InstanceHolder {
        public static final SystemClock INSTANCE = new SystemClock(1L);

        private InstanceHolder() {
        }
    }
}
