package com.europa.smallTalk;

import com.europa.smallTalk.im.msg.Msg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
public class ClientWithObjectStreamTests {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 9999);
            while (true) {
                Scanner scanner = new Scanner(System.in);
                String sendMsgContent = scanner.nextLine();
                // 发送消息到服务器
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                Msg msg = Msg.connect("小冯", "小林");
                msg.setContent(sendMsgContent);
                oos.writeObject(msg);

                // 接收服务器返回的消息
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Object obj = ois.readObject();
                if (obj instanceof Msg) {
                    Msg msgRec = (Msg) obj;
                    System.out.println("服务端返回" + msgRec);
                    if (msgRec.getCode() == 500) {
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
