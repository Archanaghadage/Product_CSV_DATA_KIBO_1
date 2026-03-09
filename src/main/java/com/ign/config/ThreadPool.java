package com.ign.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPool {

    @Bean
    public TaskExecutor taskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(3);      // Minimum number of threads
        executor.setMaxPoolSize(5);       // Maximum number of threads
        executor.setQueueCapacity(10);    // Queue size for waiting tasks
        executor.setThreadNamePrefix("product-thread-");
        executor.initialize();

        return executor;
    }
}