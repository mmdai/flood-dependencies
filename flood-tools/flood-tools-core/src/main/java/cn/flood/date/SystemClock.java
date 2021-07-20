package cn.flood.date;

import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor((runnable) -> {
            Thread thread = new Thread(runnable, "System Clock");
            thread.setDaemon(true);
            return thread;
        });
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
