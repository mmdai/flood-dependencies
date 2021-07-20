package cn.flood.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * 线程池工厂
 *
 * @author mmdai
 * @date 2019年7月13日
 */
public final class ThreadPoolFactory {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolFactory.class);

    private static ThreadPoolFactory factory = new ThreadPoolFactory();

    //线程池默认配置设置
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 20;
    private static final long KEEP_ALIVE_TIME = 60L;

    private ExecutorService executorService;

    private ThreadPoolFactory() {
        executorService = Executors.newCachedThreadPool();
    }

    private static class LazyHolder {
        //懒加载
        private static final ThreadPoolFactory INSTANCE = new ThreadPoolFactory();
    }

    public static final ExecutorService getInstance() {
        return LazyHolder.INSTANCE.executorService;
    }

    /**
     * 创建一个默认的线程池
     *
     * @return
     */
    public ExecutorService getDefaultThreadPool() {
        //配置最大队列容量
        BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>(Integer.MAX_VALUE);
        return getCustomThreadPool(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, blockingQueue);
    }

    /**
     * 创建一个简单的线程池
     *
     * @return
     */
    public ExecutorService getSimpleThreadPool(int corePoolSize, int maximumPoolSize) {
        return getCustomThreadPool(corePoolSize, maximumPoolSize, KEEP_ALIVE_TIME, new LinkedBlockingQueue<Runnable>());
    }

    /**
     * 创建一个指定队列的线程池
     *
     * @return
     */
    public ExecutorService getCustomQueueThreadPool(int corePoolSize, int maximumPoolSize, BlockingQueue<Runnable> blockingQueue) {
        return getCustomThreadPool(corePoolSize, maximumPoolSize, KEEP_ALIVE_TIME, blockingQueue);
    }

    /**
     * 创建可跟踪任务状态的执行器
     *
     * @return
     */
    public ExecutorCompletionService getCompletionService(ExecutorService executorService) {
        return new ExecutorCompletionService(executorService);
    }

    /**
     * 创建一个定制化的线程池
     *
     * @return
     */
    public ExecutorService getCustomThreadPool(int corePoolSize,
                                               int maximumPoolSize,
                                               Long keepAliveTime,
                                               BlockingQueue<Runnable> blockingQueue) {
        logger.info("开始初始化线程池[corePoolSize={},maximumPoolSize={},keepAliveTime={}s]...", corePoolSize, maximumPoolSize, keepAliveTime);

        RejectedExecutionHandler rejectedExecutionHandler = (Runnable r, ThreadPoolExecutor executor) -> {
            logger.error("线程池已满，任务被丢弃...........................");
            return;
        };
        ExecutorService executorService = new ThreadPoolExecutor(corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                blockingQueue,
                rejectedExecutionHandler);
        logger.info("初始化线程池完成！");
        return executorService;
    }

    /**
     * 等待任务执行完成 并 释放连接池
     *
     * @param futureList
     * @param completionService
     * @param <V>
     */
    public <V> void awaitTasksFinished(List<Future> futureList, CompletionService<V> completionService) {
        try {
            if (!CollectionUtils.isEmpty(futureList) && completionService != null) {
                logger.info("等待批量任务[{}]执行。。。", futureList.size());
                for (int n = 0; n < futureList.size(); n++) {
                    Future future = completionService.take();
                    if (future != null) {
                        future.get();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("多线程获取结果异常: {}", e);
        }
    }

    /**
     * 关闭线程池
     *
     * @param executorService
     */
    public void shutdown(ExecutorService executorService) {
        try {
            if (executorService != null) {
                logger.info("关闭线程池:{}", executorService);
                executorService.shutdown();
            }
        } catch (Exception e) {
            if (!executorService.isTerminated()) {
                executorService.shutdownNow();
            }
        } finally {
            try {
                if (executorService != null && !executorService.isShutdown()) {
                    executorService.shutdown();
                }
            } catch (Exception e) {
                logger.error("线程池关闭异常：{}", e);
            }
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService = new ThreadPoolFactory().getCustomThreadPool(2, 5, 5L, new LinkedBlockingQueue<Runnable>());
        for (int j = 0; j < 10; j++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    Integer i = new Random().nextInt(2) + 1;
                    Long start = System.currentTimeMillis();
                    String name = Thread.currentThread().getName();
                    System.out.println(name + " " + i + " 开始");
                    try {
                        Thread.sleep(Long.valueOf(i * 3000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(name + " " + i + " 结束,花费 " + (System.currentTimeMillis() - start) + "s");
                }
            });
        }
    }

}