package bufferfile;

import android.os.Message;

import java.io.IOException;

import connect.SocketMessage;
import global.Constant;
import global.MsgValue;
import utils.FileUtil;

import static com.example.mroot.ncfilesharing.MainActivity.handler;

/**
 * Created by mroot on 2018/4/13.
 */

public class SolveSocketMsg {
    private static final SolveSocketMsg ourInstance = new SolveSocketMsg();

    public static SolveSocketMsg getInstance() {
        return ourInstance;
    }

    private SolveSocketMsg() {
    }

    //接收MainActivity发送的socketMessage信息
    public void dealMsgEvent(SocketMessage socketMessage) {
        int msgType = socketMessage.getMsgType();
        String fileName = socketMessage.getFileName();
        byte[] fileData = socketMessage.getFileData();
        sendMessage(MsgValue.SHOW_MSG, "接收到" + fileName);

        FileUtil.write(Constant.DATA_FOLDER_PATH + "/" + fileName, fileData);


        //告知对方已经接收完毕
        SocketMessage response = new SocketMessage();
        response.setFile(false);
        response.setSocket(socketMessage.getSocket());
        response.setFileData("我已经接收完文件了，多谢！".getBytes());
        sendMessage(MsgValue.SOLVE_RESPONSE, response);


    }


    //本类发送消息的方法
    private void sendMessage(int what, Object obj) {
        if (handler != null) {
            Message.obtain(handler, what, obj).sendToTarget();
        }
    }

}
