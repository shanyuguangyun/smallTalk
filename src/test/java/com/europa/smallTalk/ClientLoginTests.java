package com.europa.smallTalk;

import com.europa.smallTalk.im.msg.Code;
import com.europa.smallTalk.im.msg.Msg;
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
                    login.setFrom(1);
                    login.setTo(-1);
                    login.setCode(Code.LOGIN.getCode());
                    oos.writeObject(login);
                } else if ("stop".equals(sendMsgContent)){
                    Logout logout = new Logout();
                    logout.setFrom(1);
                    logout.setTo(-1);
                    logout.setCode(Code.LOGOUT.getCode());
                    oos.writeObject(logout);
                    break;
                } else {
                    ReadOrWrite readOrWrite = new ReadOrWrite();
                    readOrWrite.setContent(sendMsgContent);
                    readOrWrite.setFrom(1);
                    readOrWrite.setCode(Code.READ_WRITE.getCode());
                    readOrWrite.setTo(2);
                    oos.writeObject(readOrWrite);
                }


                // 接收服务器返回的消息
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Object obj = ois.readObject();
                if (obj instanceof ReadOrWrite) {
                    ReadOrWrite msgRec = (ReadOrWrite) obj;
                    System.out.println("服务端返回" + msgRec);
                    if ("登出成功".equals(msgRec.getContent())) {
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
