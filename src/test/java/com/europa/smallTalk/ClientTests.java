package com.europa.smallTalk;

import com.europa.smallTalk.im.msg.Msg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

/**
 * @author fengwen
 * @date 2023/8/3
 * @description TODO
 **/
public class ClientTests {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 9999);
            while (true) {
                Scanner scanner = new Scanner(System.in);
                String sendMsgContent = scanner.nextLine();
                // 发送消息到服务器
                DataOutputStream dataOs = new DataOutputStream(socket.getOutputStream());
                String msg = Msg.connect("小冯", "小林").msg(sendMsgContent);
                dataOs.writeUTF(msg);

                // 接收服务器返回的消息
                DataInputStream dataIs = new DataInputStream(socket.getInputStream());
                String msgReceivedStr = dataIs.readUTF();
                System.out.println("服务端返回" + msgReceivedStr);
                Msg msgReceived = Msg.toMsg(msgReceivedStr);
                // 消息code为500时，为服务器主动关闭了连接
                assert msgReceived != null;
                if (msgReceived.getCode() == 500) {
                    socket.close();
                    break;
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
