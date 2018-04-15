package bufferfile;

import com.example.mroot.ncfilesharing.MainActivity;

import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import connect.SMsgType;
import connect.SocketMessage;
import global.Constant;
import global.MsgValue;
import utils.FileUtil;


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

        switch (msgType) {
            //客户socket连接  给他发送xml文件
            case SMsgType.TYPE_CLIENT_CONNECT:
                solveClientConnect(socketMessage);
                break;
            //文件请求  fileData指的是请求的PartEFile的no值
            case SMsgType.TYPE_REQUEST:
                //取数据给请求方
                getEncodeData(socketMessage);

                break;

            //接收到xml文件  得到对方当前的编码文件信息
            case SMsgType.TYPE_XML:
                solveXmlFile(socketMessage);

                break;

            //编码数据文件
            case SMsgType.TYPE_EFILE:
                String fileName = socketMessage.getFileName();
                byte[] fileData = socketMessage.getFileData();
                ourEncodeFile.addFile(fileName, fileData);
                socketMessage.clearExceptSocket();
                //查看对方是否还有对自己有用的数据
                getUsefulNos(socketMessage.getSocket());
                break;
            default:
                break;
        }

    }

    //处理用于加入
    private void solveClientConnect(SocketMessage socketMessage) {
        SocketMessage socketMessage1 = new SocketMessage();
        socketMessage1.setSocket(socketMessage.getSocket());
        socketMessage1.setMsgType(SMsgType.TYPE_XML);

        String xmlFilePath = ourEncodeFile.getEncodeFilePath() + "/xml.txt";

        MainActivity.sendMsg2UIThread(MsgValue.SOLVE_RESPONSE, socketMessage1);
    }

    //获取编码文件
    private void getEncodeData(final SocketMessage socketMessage) {
        byte[] bt_nos = socketMessage.getBt_nos();
        for (byte bt_no : bt_nos) {
            //取数据
            for (final PartEFile partEFile : ourEncodeFile.getPartEFileList()) {
                if (partEFile.getNo() == bt_no) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String sendFilePath = partEFile.getSendBuffer();
                            if (sendFilePath != null) {
                                //发送给主活动
                                //再传给socket发送
                                SocketMessage socketMessage1 = new SocketMessage();
                                socketMessage1.setSocket(socketMessage.getSocket());
                                socketMessage1.setMsgType(SMsgType.TYPE_EFILE);
                                socketMessage1.setFilePath(sendFilePath);
                                MainActivity.sendMsg2UIThread(MsgValue.SOLVE_RESPONSE, socketMessage1);
                            }
                        }
                    }).start();
                    break;
                }
            }
        }

    }

    //处理xml文件
    private void solveXmlFile(SocketMessage socketMessage) {
        byte[] fileData = socketMessage.getFileData();

        EncodeFile itsEncodeFile = EncodeFile.xml2obj(fileData);
        //加入list，记录xml信息
        PartnerConfig partnerConfig = new PartnerConfig();
        partnerConfig.encodeFile = itsEncodeFile;
        partnerConfig.socket = socketMessage.getSocket();
        partnerCfgList.add(partnerConfig);
        socketMessage.clearExceptSocket();
        //查看编码数据是否对自己有用
        //首先检查当前ourEncodeFile对象是不是控制的同一个文件
        String folderName = itsEncodeFile.getFolderName();
        //如果当前ourEncodeFile控制的文件和发来的不一致
        if (!(ourEncodeFile.getFolderName().equals(folderName))) {
            //不相同,则在文件中查找
            String folderPath = Constant.DATA_TEMP_PATH + "/" + folderName;
            if (new File(folderPath).exists()) {
                //文件夹存在  则恢复控制
                String xmlFilePath = folderPath + "/xml.txt";
                ourEncodeFile = EncodeFile.xml2obj(xmlFilePath);
            } else {
                //不存在则clone对象
                ourEncodeFile = EncodeFile.clone(itsEncodeFile);
            }
        }
        getUsefulNos(itsEncodeFile, socketMessage.getSocket());
    }


    private void getUsefulNos(EncodeFile itsEncodeFile, Socket socket) {
        //找到有用的parts
        byte[] bt_nos = ourEncodeFile.checkUsefulParts(itsEncodeFile);
        SocketMessage socketMessage1 = new SocketMessage();
        socketMessage1.setSocket(socket);
        socketMessage1.setMsgType(SMsgType.TYPE_REQUEST);

        MainActivity.sendMsg2UIThread(MsgValue.SOLVE_RESPONSE, socketMessage1);
    }

    //重载方法
    private void getUsefulNos(Socket socket) {
        for (PartnerConfig partnerConfig : partnerCfgList) {
            if (partnerConfig.socket == socket) {
                getUsefulNos(partnerConfig.encodeFile, socket);
                break;
            }
        }
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
        ourEncodeFile = EncodeFile.xml2obj(bt_xml);
    }


    //用来存储连接对象的编码文件配置
    //便于再次请求时使用
    private class PartnerConfig {
        public Socket socket;
        public EncodeFile encodeFile;
    }

}
