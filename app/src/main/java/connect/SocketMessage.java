package connect;

import java.net.Socket;

/**
 * Created by mroot on 2018/4/13.
 */

public class SocketMessage {

    private int msgType;

    //文件名称
    private String fileName;
    //文件内容
    private byte[] fileData;
    //用作记录是哪个客户发送的信息
    private Socket socket;


    //请求的no编号数组
    private byte[] bt_nos;

        //SolveEFileChange处理完message，返回信息时使用
    private String filePath;

    public void clearExceptSocket() {
        msgType = 0;
        fileName = null;
        fileData = null;
        bt_nos = null;
        //释放内存
        System.gc();
    }

    /**
     * getter
     * setter
     */
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

    public byte[] getBt_nos() {
        return bt_nos;
    }

    public void setBt_nos(byte[] bt_nos) {
        this.bt_nos = bt_nos;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
