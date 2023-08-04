package com.europa.smallTalk;

import com.europa.smallTalk.im.msg.Result;
import com.europa.smallTalk.im.msg.enums.RequestCode;
import com.europa.smallTalk.im.msg.enums.ResponseCode;
import com.europa.smallTalk.im.msg.login.Login;
import com.europa.smallTalk.im.msg.logout.Logout;
import com.europa.smallTalk.im.msg.readWrite.ReadOrWrite;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

/**
 * @author fengwen
 * @date 2023/8/3
 * @description TODO
 **/
public class ClientLoginTests {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 9999);
            while (true) {
                Scanner scanner = new Scanner(System.in);
                String sendMsgContent = scanner.nextLine();
                // 发送消息到服务器
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                // 模拟登录
                if ("login".equals(sendMsgContent)) {
                    Login login = new Login();
                    login.setCaptcha("1212");
                    login.setPassword("password");
                    login.setFrom(2);
                    login.setTo(-1);
                    login.setCode(RequestCode.LOGIN.getCode());
                    oos.writeObject(login);
                } else if ("stop".equals(sendMsgContent)){
                    Logout logout = new Logout();
                    logout.setFrom(1);
                    logout.setTo(-1);
                    logout.setCode(RequestCode.LOGOUT.getCode());
                    oos.writeObject(logout);
                    break;
                } else {
                    ReadOrWrite readOrWrite = new ReadOrWrite();
                    readOrWrite.setContent(sendMsgContent);
                    readOrWrite.setFrom(2);
                    readOrWrite.setCode(RequestCode.READ_WRITE.getCode());
                    readOrWrite.setTo(1);
                    oos.writeObject(readOrWrite);
                }


                // 接收服务器返回的消息
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Object obj = ois.readObject();
                if (obj instanceof Result<?>) {
                    Result<?> msgRec = (Result<?>) obj;
                    System.out.println("服务端返回" + msgRec);
                    if (ResponseCode.LOGOUT.getCode().equals(msgRec.getCode())) {
                        socket.close();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            // 服务器挂了
            System.out.println("socket reset by peer.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
