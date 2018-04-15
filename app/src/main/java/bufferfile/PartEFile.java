package bufferfile;

import android.util.Log;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import nc.NCUtils;
import utils.FileUtil;
import utils.IntAndBytes;

/**
 * Created by mroot on 2018/4/11.
 */
@XStreamAlias("PartEFile")
class PartEFile {
    //编号
    @XStreamAsAttribute
    private int no;

    private int rightFileLen;  //每个编码文件的长度

    //在编码时向文件尾部添加了几个0,恢复文件时需要去除尾部的0
    //只对最后一个部分文件生效
    @XStreamAlias("end0Num")
    private int _endwith0_num = 0;

    //存储系数矩阵
    //如果存储byte[]，在转化为字符串时，显示的是字符
    private int[][] coefMatrix = new int[0][0];

    //待发送文件的名字
    private String sendBFileName;

    @XStreamOmitField
    private int k;
    @XStreamOmitField
    private String partFilePath;
    @XStreamOmitField
    private String pieceFilePath;   //存放编码文件
    @XStreamOmitField
    private String sendBufferPath;  //维护一个再编码文件，等待发送

    public PartEFile(int no, int k, String encodeFilePath) {
        this.no = no;
        this.k = k;
        partFilePath = encodeFilePath + "/" + no;
        FileUtil.createFolder(partFilePath);
        pieceFilePath = partFilePath + "/piece";
        FileUtil.createFolder(pieceFilePath);
        sendBufferPath = partFilePath + "/sendBuffer";
        FileUtil.createFolder(sendBufferPath);
    }

    /**
     * 编码，用单位矩阵进行的拼接
     *
     * @param bis
     * @param partFileLen
     */
    public void encode(BufferedInputStream bis, int partFileLen) {
        //进行网络编码的列数
        int per_len = partFileLen / k + (partFileLen % k != 0 ? 1 : 0);
        rightFileLen = 1 + k + per_len;

        for (int i = 0; i < k; ++i) {
            //byte数组默认初始值为0
            byte[] data = new byte[rightFileLen];
            data[0] = (byte) k;
            data[i + 1] = 1;
            //计算从文件中读取的字节数
            int file_len = per_len;
            if (i == (k - 1)) {
                file_len = partFileLen - (k - 1) * per_len;
                _endwith0_num = per_len - file_len;
            }

            try {
                bis.read(data, k + 1, file_len);//
                //写入文件
                String filePath = pieceFilePath + "/" + getTimeAsFileName();
                FileUtil.write(filePath, data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //添加系数矩阵（单位矩阵）
        //创建时默认是一个0矩阵
        coefMatrix = new int[k][k];
        for (int i = 0; i < k; i++) {
            coefMatrix[i][i] = 1;
        }

        reencode();
        //进行再编码
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.i(Constant.TEST_FLAG,"再编码线程启动");
//                reencode();
//            }
//        }).start();
    }

    /**
     * 对文件进行再编码
     */
    public void reencode() {
        sendBFileName = "";
        List<File> fileList = FileUtil.getFileList(pieceFilePath);
        int fileNum = fileList.size();
        //当有0个文件或1个文件时，不用进行再编码
        if (fileNum == 0) {
            return;
        }
        if (fileNum == 1) {
            sendBFileName = fileList.get(0).getName();
            return;
        }
        //从文件读取数据到数组
        byte[] fileData = new byte[fileNum * rightFileLen];
        for (int i = 0; i < fileNum; i++) {
            File file = fileList.get(i);
            try {
                BufferedInputStream bf = new BufferedInputStream(new FileInputStream(file));
                bf.read(fileData, i * rightFileLen, rightFileLen);
                bf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //再编码
        byte[] reencodeData = NCUtils.reencode(fileData, fileNum, rightFileLen);
        String fileNameTemp = getTimeAsFileName();
        String reencodeFilePath = sendBufferPath + "/" + fileNameTemp;
        //写入文件
        FileUtil.write(reencodeFilePath, reencodeData);

        sendBFileName = fileNameTemp;
        //通知gc回收内存
        System.gc();
    }

    /**
     * 解码
     *
     * @return
     */
    public File decode() {
        String originPartFile = partFilePath + "/" + no + ".opf";
        File ofile = new File(originPartFile);
        if (ofile.exists()) {
            return ofile;
        }
        List<File> fileList = FileUtil.getFileList(pieceFilePath);
        int fileNum = fileList.size();
        if (fileNum < k) {
            //文件数目不足，无法解码
            return null;
        }
        //从文件读取数据到数组
        byte[] fileData = new byte[k * rightFileLen];
        for (int i = 0; i < k; i++) {
            File file = fileList.get(i);
            try {
                BufferedInputStream bf = new BufferedInputStream(new FileInputStream(file));
                bf.read(fileData, i * rightFileLen, rightFileLen);
                bf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //解码
        byte[] originData = NCUtils.decode(fileData, k, rightFileLen);
        //写入文件
        FileUtil.write(originPartFile, originData,
                0, originData.length - _endwith0_num, false);
        //通知gc回收内存
        System.gc();

        return ofile;
    }

    /**
     * 检查对方的文件是否对自己有用
     */
    public boolean isUseful(int[][] itsCoef) {
        //
        int oldRank = coefMatrix.length;
        int Row = coefMatrix.length + itsCoef.length;
        byte[][] testCoef = new byte[Row][k];
        for (int i = 0; i < Row; i++) {
            if (i < coefMatrix.length) {
                for (int j = 0; j < k; j++) {
                    testCoef[i][j] = (byte) coefMatrix[i][j];
                }
            } else {
                for (int j = 0; j < k; j++) {
                    testCoef[i][j] = (byte) itsCoef[i][j];
                }
            }
        }
        //检查秩   秩序增加则说明数据有用
        int newRank = NCUtils.getRank(testCoef);
        return newRank > oldRank;
    }

    /**
     * 添加文件
     * 更改了文件信息，需要互斥操作
     *
     * @param fileName
     * @param fileData
     * @return
     */
    public synchronized boolean addPieceFile(String fileName, byte[] fileData) {
        //文件长度
        int fileLen = fileData.length;
        if (fileLen != rightFileLen || (k != fileData[0])) {
            return false;
        }
        //检查秩
        int row = coefMatrix.length;
        int oldRank = row;
        //不再需要文件
        if (row == k) {
            return false;
        }
        //从文件数据中取出编码系数
        byte[] fileCoef = new byte[k];
        for (int i = 0; i < k; i++) {
            fileCoef[i] = fileData[i + 1];
        }
        //组装测试矩阵
        int Row = row + 1;
        byte[][] testCoef = new byte[Row][k];
        for (int i = 0; i < Row; i++) {
            if (i == (Row - 1)) {
                System.arraycopy(fileCoef, 0, testCoef[i], 0, k);
            }
            for (int j = 0; j < k; j++) {
                testCoef[i][j] = (byte) coefMatrix[i][j];
            }
        }
        int newRank = NCUtils.getRank(testCoef);
        if (newRank > oldRank) {
            //执行添加
            String filePath = pieceFilePath + "/" + fileName;
            FileUtil.write(filePath, fileData);
            coefMatrix = IntAndBytes.byte2Arr_int2Arr(testCoef);
            return true;
        }
        return false;
    }


    /**
     * 获取时间作为文件名
     * 只对对象加锁就可以
     *
     * @return
     */
    private synchronized String getTimeAsFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmssSSS");//"yyyy-MM-dd HH:mm:ss:SSS"
        Date curDate = new Date();
        String strTime = format.format(curDate);
        //为了保证每次取出的值唯一
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String ncFileName = no + "." + strTime + ".nc";
        return ncFileName;
    }

    /**
     * 获取到发送缓存文件夹中的文件
     *
     * @return
     */
    public synchronized String getSendBuffer() {
        if (sendBFileName.equals("") || sendBFileName == null) {
            //说明正在编码
            if (sendBFileName.equals("")) {
                Log.i("hanhai", "进入到等待再编码循环");
                long start = System.currentTimeMillis();
                while (sendBFileName.equals("")) {
                    //等待再编码
                    try {
                        Thread.sleep(10);
                        long end = System.currentTimeMillis();
                        if ((end - start) > 10 * 1000) {
                            return null;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                reencode();
            }
        }
        final String sendFilePath = sendBufferPath + "/" + sendBFileName;
        //在重新执行再编码生成
        sendBFileName = "";
        new Thread(new Runnable() {
            @Override
            public void run() {
                reencode();
            }
        }).start();

        return sendFilePath;
    }

    /**
     * get and set
     */
    public int getNo() {
        return no;
    }

    public void setK(int k) {
        this.k = k;
    }

    public void setPartFilePath(String partFilePath) {
        this.partFilePath = partFilePath;
    }

    public void setPieceFilePath(String pieceFilePath) {
        this.pieceFilePath = pieceFilePath;
    }

    public void setSendBufferPath(String sendBufferPath) {
        this.sendBufferPath = sendBufferPath;
    }

    public int[][] getCoefMatrix() {
        return coefMatrix;
    }
}
