package com.europa.smallTalk.im.helper;

import com.europa.smallTalk.im.conn.Session;
import com.europa.smallTalk.im.conn.SessionHolder;
import com.europa.smallTalk.im.msg.Result;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author fengwen
 * @date 2023/8/4
 * @description 返回数据帮助类
 **/
@Slf4j
public class ResultWriter {

    /**
     * 此方法写回要求已登录的用户，否则从ThreadLocal中获取不到session，无法正常写回数据
     *
     * @param result
     */
    public static void write(Result<?> result) {
        Session session = SessionHolder.get();
        Socket socket = session.getSocket();
        if (socket != null) {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(result);
            } catch (IOException e) {
                log.error("数据写回失败", e);
            }
        } else {
            log.error("数据写回失败，socket异常");
        }
    }

    /**
     * 此方法提供给未登录或定向写入指定socket
     *
     * @param result
     * @param socket
     */
    public static void write(Result<?> result, Socket socket) {
        if (socket == null) {
            log.error("数据写回时socket不能为空");
            return;
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(result);
        } catch (IOException e) {
            log.error("数据写回失败", e);
        }
    }
}
