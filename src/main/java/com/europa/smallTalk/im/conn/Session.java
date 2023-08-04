package com.europa.smallTalk.im.conn;

import com.europa.smallTalk.data.entity.User;
import lombok.Data;

import java.net.Socket;

/**
 * @author fengwen
 * @date 2023/8/4
 * @description 会话
 * @version 1.3
 **/
@Data
public class Session {

    /** 用户 **/
    private User user;

    /** socket **/
    private Socket socket;

}
