package cn.flood.tools.async;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;


/**
 * 
* <p>Title: AsyncExecutorConfiguration</p>  
* <p>Description: 配置异步线程池，在调用函数前注入@Async则为异步执行函数</p>  
* @author mmdai  
* @date 2018年8月13日
 */
@AutoConfiguration
@EnableAsync // 开启异步任务支持
@AutoConfigureAfter({AsyncProperties.class})
@EnableConfigurationProperties({AsyncProperties.class})
public class AsyncExecutorConfiguration implements AsyncConfigurer, SchedulingConfigurer {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
//	/** Set the ThreadPoolExecutor's core pool size. */
//	@Value("${async.corePoolSize:50}")
//	private int corePoolSize;
//	/** Set the ThreadPoolExecutor's maximum pool size. */
//	@Value("${async.maxPoolSize:50}")
//	private int maxPoolSize;
//	/** Set the capacity for the ThreadPoolExecutor's BlockingQueue. */
//	@Value("${async.queueCapacity:1024}")
//	private int queueCapacity;
//
//	@Value("${async.keepAliveSeconds:10}")
//	private int keepAliveSeconds;

	@Autowired
	private AsyncProperties asyncProperties;


	public Executor customAsync() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(asyncProperties.getCorePoolSize());
		executor.setMaxPoolSize(asyncProperties.getMaxPoolSize());
		executor.setQueueCapacity(asyncProperties.getQueueCapacity());
		executor.setThreadNamePrefix("flood-async-executor-");
		executor.setKeepAliveSeconds(asyncProperties.getKeepAliveSeconds());

		// rejection-policy：当pool已经达到max size的时候，如何处理新任务
		// CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.initialize();
		return executor;
	}

	/**
	 * 覆盖默认Executor 配置
	 */
	@Override
	public Executor getAsyncExecutor() {
		logger.debug("Creating Async Task Executor");
		return this.customAsync();
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new AsyncUncaughtExceptionHandlerEx();
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		// TODO Auto-generated method stub
		TaskScheduler scheduler = this.taskScheduler();
		taskRegistrar.setTaskScheduler(scheduler);
	}


	public ThreadPoolTaskScheduler taskScheduler() {

		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(asyncProperties.getMaxPoolSize());
//		scheduler.set
		scheduler.setThreadNamePrefix("flood-async-schedule-executor-");
		scheduler.setAwaitTerminationSeconds(60);//default 0
		scheduler.setWaitForTasksToCompleteOnShutdown(true);
		return scheduler;

	}

}
