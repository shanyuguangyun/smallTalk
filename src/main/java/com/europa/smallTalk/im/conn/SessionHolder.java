package com.europa.smallTalk.im.conn;

import com.europa.smallTalk.data.entity.User;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fengwen
 * @date 2023/8/4
 * @description 会话上下文，本着方便使用想法，由于每个线程都循环在等待read或write消息。
 * 所以每个线程单独处理一个会话，那Session信息则可以保存在ThreadLocal中。
 * @version 1.3
 **/
@Slf4j
public class SessionHolder {

    public static final ThreadLocal<Session> SESSION_LOCAL = new ThreadLocal<>();

    public static final Map<Integer, Socket> ONLINE_MAP = new ConcurrentHashMap<>();

    public static void put(User user, Socket socket) {
        Session session = new Session();
        session.setUser(user);
        session.setSocket(socket);
        SESSION_LOCAL.set(session);
        ONLINE_MAP.put(user.getId(), socket);
    }

    public static Session get() {
        return SESSION_LOCAL.get();
    }

    public static void remove(Integer userId) {
        Socket socket = ONLINE_MAP.get(userId);
        if (socket != null) {
            ONLINE_MAP.remove(userId);

            try {
                socket.close();
            } catch (IOException e) {
                log.error("socket关闭失败", e);
            }
        }
        SESSION_LOCAL.remove();
    }

}
