package connect;

import android.util.Log;

import com.example.mroot.ncfilesharing.MainActivity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import global.MsgValue;
import utils.TestCase;

/**
 * Created by mroot on 2018/4/13.
 */

public class TCPServer {
    private ServerSocket serverSocket;
    private List<Socket> socketList = new ArrayList<>();
    private ExecutorService mExecutorService = null;   //线程池

    //单例
    private static TCPServer ourInstance = new TCPServer();

    private TCPServer() {
    }

    public static TCPServer getInstance() {
        return ourInstance;
    }

    //打开服务器
    public void openServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("hanhai", "ServerSocket开启失败");
            String msg = "ServerSocket开启失败";
            MainActivity.sendMsg2UIThread(MsgValue.SHOW_MSG, msg);
            return;
        }
        Log.i("hanhai", "ServerSocket开启成功");
        String msg = "ServerSocket开启成功";
        MainActivity.sendMsg2UIThread(MsgValue.SHOW_MSG, msg);
        //创建连接等待线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 创建线程池
                mExecutorService = Executors.newCachedThreadPool();
                Socket client = null;
                //等待client连接
                while (true) {
                    try {
                        //阻塞等待连接
                        client = serverSocket.accept();
                        client.setTcpNoDelay(true);
                        socketList.add(client);
                        String msg = client.getInetAddress() + "已登录";
                        MainActivity.sendMsg2UIThread(MsgValue.SHOW_MSG, msg);
                        //启动一个新的线程来处理连接
                        mExecutorService.execute(new SocketReceiveThread(client));
                        //发送xml文件
                        SocketMessage socketMessage = new SocketMessage();
                        socketMessage.setSocket(client);
                        socketMessage.setMsgType(SMsgType.TYPE_CLIENT_CONNECT);
                        MainActivity.sendMsg2UIThread(MsgValue.CLIENT_CONNECT, socketMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }).start();
    }

    //关闭服务器
    public void closeServer() {
        for (Socket socket : socketList) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socketList.clear();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        Log.i("hanhai", "serverSocket关闭");
    }


}
