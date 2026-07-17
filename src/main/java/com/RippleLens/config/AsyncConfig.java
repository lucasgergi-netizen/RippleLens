package com.RippleLens.config;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import jakarta.annotation.PreDestroy;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private static final Logger log = LoggerFactory.getLogger(AsyncConfig.class);
    private ThreadPoolTaskExecutor executor;

    @Override
    public synchronized Executor getAsyncExecutor() {
        if (executor == null) {
            executor = new ThreadPoolTaskExecutor();
            executor.setThreadNamePrefix("ripplelens-security-");
            executor.setCorePoolSize(2);
            executor.setMaxPoolSize(4);
            executor.setQueueCapacity(100);
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            executor.setWaitForTasksToCompleteOnShutdown(true);
            executor.setAwaitTerminationSeconds(10);
            executor.initialize();
        }
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return this::logAsyncFailure;
    }

    private void logAsyncFailure(Throwable error, Method method, Object... parameters) {
        log.error("Asynchronous operation failed method={} type={}",
                method.getName(), error.getClass().getSimpleName());
    }

    @PreDestroy
    synchronized void shutdown() {
        if (executor != null) {
            executor.shutdown();
        }
    }
}
