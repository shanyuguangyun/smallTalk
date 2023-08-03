package com.europa.smallTalk.im.task;

import com.europa.smallTalk.data.dao.UserDao;
import com.europa.smallTalk.data.entity.User;
import com.europa.smallTalk.im.conn.Connections;
import com.europa.smallTalk.im.helper.DaoFactory;
import com.europa.smallTalk.im.msg.Code;
import com.europa.smallTalk.im.msg.Msg;
import com.europa.smallTalk.im.msg.login.Login;
import com.europa.smallTalk.im.msg.logout.Logout;
import com.europa.smallTalk.im.msg.readWrite.ReadOrWrite;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * 使用数据库用户操作
 *
 * @version 1.2
 */
@Data
@Slf4j
public class TaskUseUser implements Runnable {

    private Socket socket;

    public TaskUseUser() {
    }

    public TaskUseUser(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        log.debug("进入了子线程run方法");
        try {
            while (true) {
                log.debug("循环中。。。");
                ObjectInputStream dataIs = new ObjectInputStream(socket.getInputStream());
                Object obj = dataIs.readObject();

                // 这里进行分类handler
                if (obj instanceof Login) {
                    handleLogin((Login) obj);
                } else if (obj instanceof Logout) {
                    handleLogout((Logout) obj);
                } else if (obj instanceof ReadOrWrite) {
                    handleReadOrWrite((ReadOrWrite) obj);
                } else {
                    // 暂未定义其他类型的数据，所以是非法数据
                    log.warn("读取到非法数据" + obj.toString());
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

    private void handleReadOrWrite(ReadOrWrite readOrWrite) {
        log.info("收到用户对话" + readOrWrite.toString());
        // 这个逻辑是用户1给用户2发送对话
        ReadOrWrite response = new ReadOrWrite();
        response.setContent("发送成功");
        response.setFrom(-1);
        response.setTo(readOrWrite.getFrom());
        response.setCode(Code.READ_WRITE.getCode());
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(readOrWrite);
        } catch (IOException e) {
            log.error("返回客户端信息错误", e);
        }
    }

    private void handleLogout(Logout logout) {
        // 用户登出 清除socket
        ReadOrWrite readOrWrite = new ReadOrWrite();
        // 用户登出 用户记录为离线态
        readOrWrite.setContent("登出成功");
        readOrWrite.setFrom(-1);
        readOrWrite.setTo(logout.getFrom());
        readOrWrite.setCode(Code.READ_WRITE.getCode());
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(readOrWrite);
            socket.close();
        } catch (IOException e) {
            log.error("返回客户端信息错误", e);
        }
    }

    private void handleLogin(Login login) {
        ReadOrWrite readOrWrite = new ReadOrWrite();
        // 用户登录 记录为在线状态
        UserDao userDao = (UserDao) DaoFactory.getByClass(UserDao.class);
        User user = userDao.findById(login.getFrom()).orElse(null);
        if (user != null) {
            log.info("用户登录" + user.getId());
            // 添加到在线用户
            readOrWrite.setContent("登录成功");
            // 暂用-1代表服务器
        } else {
            // 用户不存在，返回给客户端，重复代码可以封装
            readOrWrite.setContent("用户不存在");
        }
        readOrWrite.setFrom(-1);
        readOrWrite.setTo(login.getFrom());
        readOrWrite.setCode(Code.READ_WRITE.getCode());
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(readOrWrite);
        } catch (IOException e) {
            log.error("返回客户端信息错误", e);
        }
    }

}
