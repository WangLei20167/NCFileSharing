package connect;

import android.util.Log;

import com.example.mroot.ncfilesharing.MainActivity;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import global.MsgValue;

/**
 * Created by mroot on 2018/4/15.
 */

class SocketReceiveThread extends Thread {
    private Socket socket;

    public SocketReceiveThread(Socket socket) {
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
            Log.i("hanhai", "创建socket读入流失败！");
            return;
        }
        //判断socket是否处于连接状态
        //当isClosed()为false，isConnected()为true时，才处于连接状态
        while (socket.isConnected() && !socket.isClosed()) {
            try {
                SocketMessage socketMessage = new SocketMessage();
                //msgType用来确定信息的类型
                int msgType = dis.readInt();
                socketMessage.setMsgType(msgType);
                //携带socket交给别的类进行单独处理
                socketMessage.setSocket(socket);
                switch (msgType) {
                    case SMsgType.TYPE_REQUEST:
                        int questLen = dis.readInt();
                        byte[] bt_nos = new byte[questLen];
                        dis.readFully(bt_nos);
                        socketMessage.setBt_nos(bt_nos);
                        //发送给UI线程
                        MainActivity.sendMsg2UIThread(MsgValue.SOCKET_MSG, socketMessage);
                        break;
                    case SMsgType.TYPE_XML:
                    case SMsgType.TYPE_EFILE:
                        socketMessage.setFileName(dis.readUTF());
                        //文件长度
                        int datalen = dis.readInt();
                        byte[] data = new byte[datalen];
                        dis.readFully(data);
                        socketMessage.setFileData(data);
                        //处理file
                        MainActivity.sendMsg2UIThread(MsgValue.SOCKET_MSG, socketMessage);
                        break;
                    default:

                        break;
                }
            } catch (SocketTimeoutException e){
                //超时异常
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
