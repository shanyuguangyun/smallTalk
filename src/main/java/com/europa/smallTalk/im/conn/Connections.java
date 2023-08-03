package com.europa.smallTalk.im.conn;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fengwen
 * @date 2023/8/3
 * @description 链接集合
 **/
@Slf4j
@Data
public class Connections {

    public static List<Socket> socketList = new ArrayList<>();

    public Connections() {
    }

    public static void remove(Socket socket) {
        log.info("客户端断开连接" + socket);
        socketList.remove(socket);
        try {
            socket.close();
        } catch (IOException e) {
            log.error("socket关闭失败", e);
        }
    }

    public static void register(Socket socket) {
        log.info("连接已注入");
        socketList.add(socket);
    }
}
