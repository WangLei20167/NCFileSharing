package bufferfile;

import android.util.Log;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.SortableFieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import global.Constant;
import utils.FileUtil;


/**
 * 程序只维护以个EncodeFile对象
 * Created by mroot on 2018/4/11.
 */
@XStreamAlias("EncodeFile")
class EncodeFile {
    //主属性
    @XStreamAsAttribute
    private String fileName;
    private int fileLen;

    @XStreamAlias("GenerationSize")
    private int k = 4;
    //存储目录
    private String encodeFilePath;

    private int partNum = 0; //切分成的部分数

    private int currentSmallPiece = 0;
    private int totalSmallPiece = 0;

    @XStreamAlias("partFileInfor")
    private List<PartEFile> partEFileList = new ArrayList<>();


    //对文件进行分片操作
    public void init(String filePath, int k) {
        this.k = k;
        File file = new File(filePath);
        this.fileName = file.getName();
        this.fileLen = (int) file.length();
        String folderName = fileName.substring(0, fileName.indexOf("."));
        this.encodeFilePath = Constant.DATA_TEMP_PATH + "/" + folderName;
        FileUtil.createFolder(encodeFilePath);
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //对文件进行切片处理,每10M一个文件
        int perPartLen = 10 * 1024 * 1024;
        partNum = fileLen / perPartLen + 1;
        //每部分的文件长度
        int rightPartLen = perPartLen;
        int restPartLen = fileLen - (partNum - 1) * perPartLen;  //最后一部分的文件长度
        for (int i = 0; i < partNum; i++) {
            int partLen;
            if (i == partNum - 1) {
                partLen = restPartLen;
            } else {
                partLen = rightPartLen;
            }
            int no = i + 1;
            PartEFile partEFile = new PartEFile(no, k, encodeFilePath);
            partEFile.encode(bis, partLen);
            partEFileList.add(partEFile);
        }

        try {
            if (bis != null) {
                bis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //当前拥有全部编码文件片
        totalSmallPiece = partNum * k;
        currentSmallPiece = totalSmallPiece;
        //object写入xml文件
        object2xml();
    }


    //恢复文件
    public void recoverFile() {
        if (currentSmallPiece != totalSmallPiece) {
            //文件数目不足，无法解码
            return;
        }
        String originFile = encodeFilePath + "/" + fileName;
        if (new File(originFile).exists()) {
            return;
        }
        File[] files = new File[partNum];
        for (PartEFile partEFile : partEFileList) {
            int no = partEFile.getNo();
            File dFile = partEFile.decode();
            if (dFile == null) {
                //有一部分文件解码失败
                return;
            } else {
                files[no - 1] = dFile;
            }
        }
        //拼接文件
        FileUtil.mergeFiles(originFile, files);

    }


    //添加文件
    public void addFile(String fileName, byte[] fileData) {
        String strNo = fileName.substring(0, fileName.indexOf("."));
        int no = Integer.parseInt(strNo);
        PartEFile partEFile1 = null;
        for (PartEFile partEFile : partEFileList) {
            if (partEFile.getNo() == no) {
                partEFile1 = partEFile;
                break;
            }
        }
        if (partEFile1 == null) {
            partEFile1 = new PartEFile(no, k, encodeFilePath);
            partEFileList.add(partEFile1);
        }
        if (partEFile1.addPieceFile(fileName, fileData)) {
            //添加文件成功
            synchronized (this) {
                currentSmallPiece += 1;
                //这里应该吧修改写入xml文件
                object2xml();
                recoverFile();
            }
        }
    }

    //查看是否有对自己有用的数据
    //如果有，返回PartEFile.no值
    public byte[] checkUsefulParts(EncodeFile itsEncodeFile) {
        for (PartEFile partEFile : itsEncodeFile.partEFileList) {
            int itsNo = partEFile.getNo();
            for (PartEFile eFile : partEFileList) {
                if(eFile.getNo()==itsNo){
                    //在PartEFile中检查秩

                }
            }
        }
    }

    //object转化为xml字符串
    public String object2xml() {
        //设置xml字段顺序
        SortableFieldKeySorter sorter = new SortableFieldKeySorter();
        sorter.registerFieldOrder(EncodeFile.class,
                new String[]{
                        "fileName",
                        "fileLen",
                        "k",
                        "encodeFilePath",
                        "partNum",
                        "currentSmallPiece",
                        "totalSmallPiece",
                        "partEFileList"
                });
        sorter.registerFieldOrder(PartEFile.class,
                new String[]{
                        "no",
                        "rightFileLen",
                        "_endwith0_num",
                        "coefMatrix",
                        "k",
                        "partFilePath",
                        "pieceFilePath",
                        "sendBufferPath"
                });
        //XStream xStream = new XStream(new DomDriver("UTF-8"));
        XStream xStream = new XStream(new Sun14ReflectionProvider(new FieldDictionary(sorter)));
        xStream.setMode(XStream.NO_REFERENCES);
        //使用注解
        xStream.processAnnotations(EncodeFile.class);
        xStream.processAnnotations(PartEFile.class);
        //转化为String，并保存入文件
        String xml = xStream.toXML(this);
        Log.i(Constant.TEST_FLAG, xml);
        //写入配置文件
        FileUtil.write(encodeFilePath + "/" + fileName + ".txt", xml.getBytes());
        return xml;
    }

    //xml转object
    public static EncodeFile xml2obj(String xml) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        //使用注解
        xStream.processAnnotations(EncodeFile.class);
        xStream.processAnnotations(PartEFile.class);
        //这个blog标识一定要和Xml中的保持一直，否则会报错
        xStream.alias("EncodeFile", EncodeFile.class);
        EncodeFile encodeFile = (EncodeFile) xStream.fromXML(xml);
        int nK = encodeFile.k;
        String encodeFilePath0 = encodeFile.encodeFilePath;
        //恢复被忽略的成员变量值
        for (PartEFile partEFile : encodeFile.partEFileList) {
            int no = partEFile.getNo();
            partEFile.setK(nK);
            String partFilePath = encodeFilePath0 + "/" + no;
            partEFile.setPartFilePath(partFilePath);
            partEFile.setPieceFilePath(partFilePath + "/piece");
            partEFile.setSendBufferPath(partFilePath + "/sendBuffer");
        }
        return encodeFile;
    }

}
