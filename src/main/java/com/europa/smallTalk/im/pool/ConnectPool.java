package com.europa.smallTalk.im.pool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author fengwen
 * @date 2023/8/3
 * @description 连接线程池定义
 **/
@Slf4j
@Configuration
public class ConnectPool {

    @Bean("connectPoolExecutor")
    public ThreadPoolExecutor connectPoolExecutor() {
        return new ThreadPoolExecutor(10,
                100,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10000),
                new ThreadFactory() {
                   final AtomicInteger aInt = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r,"conn-thread-" + aInt.getAndIncrement());
                    }
                },
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        log.error("服务器负载已达上限，线程池已满");
                    }
                });
    }
}
