package connect;

import android.util.Log;

import com.example.mroot.ncfilesharing.MainActivity;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import global.MsgValue;

/**
 * Created by mroot on 2018/4/13.
 */

public class TCPClient {
    private Socket socket;
    private DataInputStream dis;   //接收
    //private DataOutputStream dos;
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
                    //设置读超时  10S
                    socket.setSoTimeout(10 * 1000);
                    dis = new DataInputStream(socket.getInputStream());
                   // dos = new DataOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("hanhai", "连接SocketServer失败");
                    return;
                }
                Log.i("hanhai", "连接成功");
                String msg = "ServerSocket连接成功";
                MainActivity.sendMsg2UIThread(MsgValue.SHOW_MSG, msg);
                //启动一个接收线程
                new SocketReceiveThread(socket).start();
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




}
