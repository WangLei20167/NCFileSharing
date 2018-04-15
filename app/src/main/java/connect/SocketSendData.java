package connect;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * TCPServer 和 TCPClient的发送
 * 因为是一样的，所以都在此处理
 * Created by mroot on 2018/4/15.
 */

public class SocketSendData {
    //
    public static void checkRespond(SocketMessage socketMessage) {
        int msgType = socketMessage.getMsgType();
        switch (msgType) {
            //发送文件请求
            case SMsgType.TYPE_REQUEST:
                sendFileQuest(socketMessage);
                break;
            //发送文件
            case SMsgType.TYPE_XML:
            case SMsgType.TYPE_EFILE:
                sendFile(socketMessage);
                break;
            default:
                break;
        }
    }

    private static void sendFileQuest(SocketMessage socketMessage) {
        try {
            Socket socket = socketMessage.getSocket();
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            //发送文件标识
            dos.writeInt(socketMessage.getMsgType());
            byte[] bt_nos=socketMessage.getBt_nos();
            dos.writeInt(bt_nos.length);
            dos.write(bt_nos);

            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //发送文件
    private static void sendFile(final SocketMessage socketMessage) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = socketMessage.getSocket();
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    File file = new File(socketMessage.getFilePath());
                    int msgType = socketMessage.getMsgType();
                    //发送文件标识
                    dos.writeBoolean(true);
                    //fileType
                    dos.writeInt(msgType);
                    //fileName
                    dos.writeUTF(file.getName());
                    //dataLen
                    dos.writeInt((int) file.length());
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                    byte[] arr = new byte[1024 * 8];
                    int len;
                    while ((len = bis.read(arr)) != -1) {
                        dos.write(arr, 0, len);
                        dos.flush();
                    }
                    bis.close();
                    //是否删除发送缓存中的文件
                    if (msgType == SMsgType.TYPE_EFILE) {
                        file.delete();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void sendCode(SocketMessage socketMessage) {
        try {
            Socket socket = socketMessage.getSocket();
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            //发送文件标识
            dos.writeBoolean(false);
            //fileType
            //dos.writeInt(1);
            //
            dos.writeUTF("接收完毕，多谢你");
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
