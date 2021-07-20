package cn.flood.delay.threads;

import cn.flood.delay.core.RedisDelayQueueContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description 关闭线程池
 * @Author daimi
 * @Date 2019/8/3 10:28 AM
 **/
public class ShutdownThread {

    private static final Logger logger = LoggerFactory.getLogger(RedisDelayQueueContext.class);

    /**
     * shutdownNow 终止线程的方法是通过调用Thread.interrupt()方法来实现的
     * 如果线程中没有sleep 、wait、Condition、定时锁等应用, interrupt()方法是无法中断当前的线程的。
     * 上面的情况中断之后还是可以再执行finally里面的方法的;
     * 但是如果是其他的情况 finally是不会被执行的
     * @param executor
     */
    public static void closeExecutor(ExecutorService executor, String executorName) {
        try {
            //新的任务不进队列
            executor.shutdown();
            //给10秒钟没有停止完强行停止;
            if(!executor.awaitTermination(20, TimeUnit.SECONDS)) {
                logger.warn("线程池: {},{}没有在20秒内关闭,则进行强制关闭",executorName,executor);
                List<Runnable> droppedTasks = executor.shutdownNow();
                logger.warn("线程池: {},{} 被强行关闭,阻塞队列中将有{}个将不会被执行.", executorName,executor,droppedTasks.size() );
            }
            logger.info("线程池:{},{} 已经关闭...",executorName,executor);
        }  catch (InterruptedException e) {
            logger.info("线程池:{},{} 打断...",executorName,executor);
        }
    }

}
