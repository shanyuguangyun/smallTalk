package com.europa.smallTalk.im.task;

import com.europa.smallTalk.im.conn.Connections;
import com.europa.smallTalk.im.msg.Msg;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * 使用ObjectInput Output stream
 * @version 1.1
 */
@Slf4j
@Data
public class TaskUseObjectStream implements Runnable {


    private Socket socket;

    public TaskUseObjectStream() {
    }

    public TaskUseObjectStream(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        log.debug("进入了子线程run方法");
        try {
            while (true) {
                log.debug("循环中。。。");
                ObjectInputStream dataIs = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream dataOs = new ObjectOutputStream(socket.getOutputStream());
                Object obj = dataIs.readObject();
                if (obj instanceof Msg) {
                    Msg msg = (Msg) obj;
                    log.info("收到的消息为:" + msg);
                    if ("stop".equals(msg.getContent())) {
                        Msg msgSend = Msg.stop("server", msg.getFrom());
                        msgSend.msg("结束");
                        dataOs.writeObject(msgSend);
                        Connections.remove(socket);
                        break;
                    } else {
                        Msg msgSend = Msg.connect("server", msg.getFrom());
                        msgSend.msg("成功");
                        dataOs.writeObject(msgSend);
                    }
                }
            }
        } catch (SocketException e) {
            log.debug("客户端强制退出", e);
            Connections.remove(socket);
        } catch (IOException e) {
            log.error("子线程处理消息错误", e);
        } catch (ClassNotFoundException e) {
            log.error("数据反序列化错误", e);
        }
    }
}
