package com.europa.smallTalk.im.task;

import com.europa.smallTalk.data.dao.UserDao;
import com.europa.smallTalk.data.entity.User;
import com.europa.smallTalk.im.conn.Connections;
import com.europa.smallTalk.im.conn.Session;
import com.europa.smallTalk.im.conn.SessionHolder;
import com.europa.smallTalk.im.helper.DaoFactory;
import com.europa.smallTalk.im.helper.ResultWriter;
import com.europa.smallTalk.im.msg.Result;
import com.europa.smallTalk.im.msg.enums.ResponseCode;
import com.europa.smallTalk.im.msg.login.Login;
import com.europa.smallTalk.im.msg.logout.Logout;
import com.europa.smallTalk.im.msg.readWrite.ReadOrWrite;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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
                // 原本socket阻塞在这里
                Object obj = dataIs.readObject();

                // 这里进行分类handler
                if (obj instanceof Login) {
                    Result<User> result = handleLogin((Login) obj);
                    // 包含登录失败，所以指定socket
                    ResultWriter.write(result, socket);
                } else if (obj instanceof Logout) {
                    Result<String> result = handleLogout((Logout) obj);
                    ResultWriter.write(result, socket);
                } else if (obj instanceof ReadOrWrite) {
                    // 未登录直接返回数据了，这里发现返回数据不大友好，理应返回后结束循环的，
                    // 但因为链接不断开的原因，继续向下执行。所以写数据的逻辑应该放在主方法内，其他方法只返回Result对象即可。
                    if (!checkLoggedIn((ReadOrWrite) obj)) {
                        Result<String> result = Result.ok("请先登录");
                        ResultWriter.write(result, socket);
                        continue;
                    }
                    Result<String> result = handleReadOrWrite((ReadOrWrite) obj);
                    ResultWriter.write(result);
                } else {
                    // 暂未定义其他类型的数据，所以是非法数据
                    log.warn("读取到非法数据" + obj.toString());
                }
            }
        } catch (SocketException e) {
            // 由于网络情况复杂，不能仅凭一个socketException就认定用户下线，应该调整为心跳或定时清理用户
            log.debug("客户端强制退出", e);
            Connections.remove(socket);
        } catch (IOException e) {
            log.error("子线程处理消息错误", e);
        } catch (ClassNotFoundException e) {
            log.error("数据反序列化错误", e);
        }
    }

    private boolean checkLoggedIn(ReadOrWrite readOrWrite) {
        Session session = SessionHolder.get();
        return session != null;
    }

    private Result<String> handleReadOrWrite(ReadOrWrite readOrWrite) {
        log.info("用户对话" + readOrWrite);
        // 将对话转发给对应人。
        // 如果说有标识能在流的基础上就能判断出是要转发给对应人员的数据就又能省两步序列化。
        // 所以考虑，反序列化成字节数组是否会好点，这样能根据某几个字节判断是不同。
        Socket targetSocket = SessionHolder.ONLINE_MAP.get(readOrWrite.getTo());
        // 说明对方用户在线
        if (targetSocket != null) {
            // 这里会出问题，因为此socket已经阻塞在读取object那了，无法使用他的output stream。直接杀掉的话会杀错进程。
            try {
                targetSocket.shutdownOutput();
            } catch (IOException e) {
                log.info("杀掉了对应targetSocket的inputStream。");
            }
            Result<ReadOrWrite> result = Result.ok(readOrWrite);
            ResultWriter.write(result, targetSocket);
        }
        // TODO 异步落库
        return Result.ok("收到消息");
    }

    private Result<String> handleLogout(Logout logout) {
        log.info("用户登出" + logout);
        SessionHolder.remove(logout.getFrom());
        return Result.ok("登出成功");
    }

    private Result<User> handleLogin(Login login) {
        log.info("用户登录" + login);
        Result<User> result;
        // 用户登录 记录为在线状态
        UserDao userDao = (UserDao) DaoFactory.getByClass(UserDao.class);
        User user = userDao.findById(login.getFrom()).orElse(null);
        if (user != null) {
            // 记录用户会话
            SessionHolder.put(user, socket);
            result = Result.ok(user);
        } else {
            // 用户不存在，返回给客户端，重复代码可以封装
            result = Result.fail(ResponseCode.SERVER_ERROR.getCode(), "用户不存在");
        }
        return result;
    }

}
