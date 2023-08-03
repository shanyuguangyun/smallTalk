package com.europa.smallTalk.im;

import com.europa.smallTalk.im.conn.Connections;
import com.europa.smallTalk.im.helper.DaoFactory;
import com.europa.smallTalk.im.task.Task;
import com.europa.smallTalk.im.task.TaskUseObjectStream;
import com.europa.smallTalk.im.task.TaskUseUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author fengwen
 * @date 2023/8/3
 * @description TODO
 **/
@Slf4j
@Component
public class Server implements ApplicationRunner {

    @Value("${im.port}")
    private Integer imPort;

    @Autowired
    private ApplicationContext applicationContext;

    @Resource
    private ThreadPoolExecutor connectPoolExecutor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 这里做简单点，由于后面要用到数据查询，将Dao层数据取出来
        Map<String, JpaRepository> beansOfRepository = applicationContext.getBeansOfType(JpaRepository.class);
        DaoFactory.register(beansOfRepository);

        // 不阻塞主线程，另开个独立线程处理serverSocket.accept
        try {
            ServerSocket serverSocket = new ServerSocket(imPort);
            log.info("IM服务器启动在端口" + imPort);
            Executors.newSingleThreadExecutor().execute(() -> {
                while (!Thread.interrupted()) {
                    try {
                        // 死循环接收客户端连接
                        Socket socket = serverSocket.accept();
                        log.info("成功建立连接" + socket);
                        Connections.register(socket);
                        // 注意这里，由于每个任务都要一直持有连接。线程不会自动停止，这里的线程池的核心数会限制用户数量。
//                        Runnable task = new Task(socket);
//                        Runnable task = new TaskUseObjectStream(socket);
                        Runnable task = new TaskUseUser(socket);
                        connectPoolExecutor.execute(task);
                    } catch (IOException e) {
                        log.error("客户端建立连接错误", e);
                    }
                }
            });
        } catch (Exception e) {
            log.error("IM服务器IO错误", e);
        }
    }
}
