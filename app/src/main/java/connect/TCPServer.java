package connect;

import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bufferfile.SolveSocketMsg;
import global.Constant;
import global.MsgValue;
import utils.FileUtil;

import static com.example.mroot.ncfilesharing.MainActivity.handler;

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
            sendMessage(MsgValue.SHOW_MSG, msg);
            return;
        }
        Log.i("hanhai", "ServerSocket开启成功");
        String msg = "ServerSocket开启成功";
        sendMessage(MsgValue.SHOW_MSG, msg);
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
                        sendMessage(MsgValue.SHOW_MSG, msg);
                        //启动一个新的线程来处理连接
                        mExecutorService.execute(new ClientThread(client));
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


    //处理与client的对话
    class ClientThread extends Thread {
        private Socket socket;

        public ClientThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            //接收
            DataInputStream dis = null;
            try {
                dis = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //判断socket是否处于连接状态
            //当isClosed()为false，isConnected()为true时，才处于连接状态
            while (socket.isConnected() && !socket.isClosed()) {
                try {
                    //用来标志发过来的信息是不是文件
                    boolean isFile = dis.readBoolean();

                    if (isFile) {
                        SocketMessage socketMessage = new SocketMessage();
                        socketMessage.setMsgType(dis.readInt());
                        socketMessage.setFileName(dis.readUTF());
                        //int fileType = dis.readInt();
                        //String fileName = dis.readUTF();
                        int datalen = dis.readInt();
                        byte[] data = new byte[datalen];
                        dis.readFully(data);
                        socketMessage.setFileData(data);
                        //处理file
                        //携带socket交给别的类进行单独处理
                        socketMessage.setSocket(socket);
                        //FileUtil.write(Constant.DATA_FOLDER_PATH + "/" + fileName, data);
                        sendMessage(MsgValue.SOCKET_MSG, socketMessage);
                    } else {
                        int code = dis.readInt();
                        //处理code
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //用来发送状态  是否关闭连接等信息
    public void sendCode(Socket socket, int code) {
        try {
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


    //本类发送消息的方法
    private void sendMessage(int what, Object obj) {
        if (handler != null) {
            Message.obtain(handler, what, obj).sendToTarget();
        }
    }
}
