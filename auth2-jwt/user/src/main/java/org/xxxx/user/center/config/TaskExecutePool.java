package org.bifu.user.center.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 自定义任务池
 * 
 */
@Configuration
public class TaskExecutePool {

	@Value("${spring.task.pool.corePoolSize}")
	private int corePoolSize;

	@Value("${spring.task.pool.maxPoolSize}")
	private int maxPoolSize;

	@Value("${spring.task.pool.keepAliveSeconds}")
	private int keepAliveSeconds;

	@Value("${spring.task.pool.queueCapacity}")
	private int queueCapacity;

	@Bean
	public Executor initTaskAsyncPool() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(keepAliveSeconds);
		executor.setKeepAliveSeconds(queueCapacity);
		executor.setThreadNamePrefix("MyExecutor-");

		// rejection-policy：当pool已经达到max size的时候，如何处理新任务
		// CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.initialize();
		return executor;
	}

	@Bean
	public SchedulingTaskExecutor initSchedulingTaskExecutor() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(100);
		scheduler.setThreadNamePrefix("MyScheduler-");
		return scheduler;
	}

}
