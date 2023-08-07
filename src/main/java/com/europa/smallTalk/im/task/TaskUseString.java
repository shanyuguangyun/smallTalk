package com.europa.smallTalk.im.task;

import com.europa.smallTalk.im.conn.Connections;
import com.europa.smallTalk.im.msg.Msg;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author fengwen
 * @date 2023/8/3
 * @description 任务
 * @version 1.1
 **/
@Slf4j
@Data
public class TaskUseString implements Runnable {


    private Socket socket;

    public TaskUseString() {
    }

    public TaskUseString(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        log.debug("进入了子线程run方法");
        try {
            while (true) {
                log.debug("循环中。。。");
                InputStream inputStream = socket.getInputStream();
                byte[] buffer = new byte[100];
                int length = 100;
                StringBuilder stb = new StringBuilder();
                while (length == 100) {
                    length = inputStream.read(buffer);
                    stb.append(new String(buffer, 0, length));
                }
                System.out.println(Thread.currentThread().getName() + "线程读取--------------------------:\n" + stb.toString());
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write("收到了消息".getBytes());
            }
        } catch (SocketException e) {
            log.debug("客户端强制退出", e);
            Connections.remove(socket);
        } catch (IOException e) {
            log.error("子线程处理消息错误", e);
        }
    }
}
