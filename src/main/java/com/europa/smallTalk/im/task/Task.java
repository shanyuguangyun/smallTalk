package com.europa.smallTalk.im.task;

import com.europa.smallTalk.im.conn.Connections;
import com.europa.smallTalk.im.msg.Msg;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author fengwen
 * @date 2023/8/3
 * @description 任务
 **/
@Slf4j
@Data
public class Task implements Runnable {

    private Socket socket;

    public Task() {
    }

    public Task(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        log.info("进入了子线程run方法");
        try {
            while (true) {
                log.info("循环中。。。");
                DataInputStream dataIs = new DataInputStream(socket.getInputStream());
                String dataStr = dataIs.readUTF();
                Msg msg = Msg.toMsg(dataStr);
                log.info("收到的消息为:" + dataStr);
                DataOutputStream dataOs = new DataOutputStream(socket.getOutputStream());
                if ("stop".equals(msg.getContent())) {
                    dataOs.writeUTF(Msg.stop("server", msg.getFrom()).msg("结束"));
                    Connections.remove(socket);
                    break;
                } else {
                    dataOs.writeUTF(Msg.connect("server", msg.getFrom()).msg("成功"));
                }
            }
        } catch (SocketException e) {
//            log.info("客户端强制退出", e);
            Connections.remove(socket);
        } catch (IOException e) {
            log.error("子线程处理消息错误", e);
        }
    }
}
