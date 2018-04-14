package bufferfile;

import android.os.Message;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import connect.SocketMessage;
import global.Constant;
import global.MsgValue;
import utils.FileUtil;

import static com.example.mroot.ncfilesharing.MainActivity.handler;

/**
 * Created by mroot on 2018/4/13.
 */

public class SolveEFileChange {
    private static final SolveEFileChange ourInstance = new SolveEFileChange();

    public static SolveEFileChange getInstance() {
        return ourInstance;
    }

    //
    private List<PartnerConfig> partnerCfgList = new ArrayList<>();
    //只维护这一个encodeFile对象
    private EncodeFile ourEncodeFile;

    private SolveEFileChange() {
        ourEncodeFile = new EncodeFile();
    }

    //接收MainActivity发送的socketMessage信息
    public void dealMsgEvent(SocketMessage socketMessage) {
        int msgType = socketMessage.getMsgType();
        String fileName = socketMessage.getFileName();
        byte[] fileData = socketMessage.getFileData();
        byte[] bt_nos = socketMessage.getBt_nos();
        switch (msgType) {
            //文件请求  fileData指的是请求的PartEFile的no值
            case SocketMessage.TYPE_REQUEST:
                //取数据给请求方
                
                break;

            //接收到xml文件  得到对方当前的编码文件信息
            case SocketMessage.TYPE_XML:
                EncodeFile encodeFile = EncodeFile.xml2obj(new String(fileData));
                PartnerConfig partnerConfig = new PartnerConfig();
                partnerConfig.encodeFile = encodeFile;
                partnerConfig.socket = socketMessage.getSocket();
                partnerCfgList.add(partnerConfig);
                //查看编码数据是否对自己有用
                socketMessage.clearExceptSocket();

                break;

            //编码数据文件
            case SocketMessage.TYPE_EFILE:
                ourEncodeFile.addFile(fileName, fileData);
                socketMessage.clearExceptSocket();
                //查看对方是否还有对自己有用的数据

                break;
            default:
                break;
        }

//        sendMessage(MsgValue.SHOW_MSG, "接收到" + fileName);
//
//        FileUtil.write(Constant.DATA_FOLDER_PATH + "/" + fileName, fileData);
//
//        //告知对方已经接收完毕
//        SocketMessage response = new SocketMessage();
//        response.setFile(false);
//        response.setSocket(socketMessage.getSocket());
//        response.setFileData("我已经接收完文件了，多谢！".getBytes());
//        sendMessage(MsgValue.SOLVE_RESPONSE, response);
    }


    //初始化encodeFile
    public void initFile(final String filePath, final int k) {
        //处理时间较长，放入线程执行
        new Thread(new Runnable() {
            @Override
            public void run() {
                ourEncodeFile.init(filePath, k);
            }
        }).start();
    }

    //从xml文件恢复encodeFile变量
    public void initFromXML(String xmlFilePath) {
        byte[] bt_xml = FileUtil.read(xmlFilePath);
        ourEncodeFile = EncodeFile.xml2obj(new String(bt_xml));
    }


    //本类发送消息的方法
    private void sendMessage(int what, Object obj) {
        if (handler != null) {
            Message.obtain(handler, what, obj).sendToTarget();
        }
    }


    //用来存储连接对象的编码文件配置
    //便于再次请求时使用
    private class PartnerConfig {
        public Socket socket;
        public EncodeFile encodeFile;
    }

}
