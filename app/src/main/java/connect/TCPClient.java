package connect;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import global.MsgValue;

import static com.example.mroot.ncfilesharing.MainActivity.handler;

/**
 * Created by mroot on 2018/4/13.
 */

public class TCPClient {
    private Socket socket;
    private DataInputStream dis;   //接收
    private DataOutputStream dos;
    //sington
    private static TCPClient ourInstance = new TCPClient();

    private TCPClient() {
    }

    public static TCPClient getInstance() {
        return ourInstance;
    }

    public void connect(final String ip, final int port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ip, port);
                    socket.setReuseAddress(true);
                    socket.setTcpNoDelay(true);
                    dis = new DataInputStream(socket.getInputStream());
                    dos = new DataOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("hanhai", "连接SocketServer失败");
                    return;
                }
                Log.i("hanhai", "连接成功");
                String msg = "ServerSocket连接成功";
                sendMessage(MsgValue.SHOW_MSG, 0, 0, msg);
                //启动一个接收线程
                new ReceiveThread().start();
            }
        }).start();

    }

    public void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    class ReceiveThread extends Thread {
        @Override
        public void run() {
            //判断socket是否处于连接状态
            //当isClosed()为false，isConnected()为true时，才处于连接状态
            while (socket.isConnected() && !socket.isClosed()) {
                try {
                    boolean isFile = dis.readBoolean();
                    if (isFile) {
                        int datalen = dis.readInt();
                        byte[] data = new byte[datalen];
                        dis.readFully(data);
                    } else {
                        String msg = dis.readUTF();
                        sendMessage(MsgValue.SHOW_MSG,0,0,msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //发送文件
    public void sendFile(final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!file.exists()) {
                        return;
                    }
                    //发送文件标识
                    dos.writeBoolean(true);
                    //fileType
                    dos.writeInt(1);
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    //用来发送状态  是否关闭连接等信息
    public void sendCode(int code) {
        try {
            //发送文件标识
            dos.writeBoolean(false);
            //fileType
            dos.writeInt(1);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //本类发送消息的方法
    private void sendMessage(int what, int arg1, int arg2, Object obj) {
        if (handler != null) {
            Message.obtain(handler, what, arg1, arg2, obj).sendToTarget();
        }
    }

}
