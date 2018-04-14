package connect;

import java.net.Socket;

/**
 * Created by mroot on 2018/4/13.
 */

public class SocketMessage {
    private boolean isFile;
    //约定好的信息类型值
    //1 表示是XML文件
    //2 表示是编码文件
    //3 表示是文件请求
    //4 表示已经不再需要信息（可以断开了）
    private int msgType;
    //文件名称
    private String fileName;
    //文件内容
    private byte[] fileData;
    //用作记录是哪个客户发送的信息
    private Socket socket;


    //getter setter
    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }
}
