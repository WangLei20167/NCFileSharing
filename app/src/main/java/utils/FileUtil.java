package utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mroot on 2018/4/11.
 */

public class FileUtil {
    /**
     * 二进制文件标准方法
     *
     * @param bFile
     * @return
     * @throws IOException
     */
    public static byte[] read(File bFile) {
        //经测试匿名文件流没关闭，没报异常
        BufferedInputStream bf = null;
        byte[] data = null;
        try {
            bf = new BufferedInputStream(new FileInputStream(bFile));
            data = new byte[bf.available()];
            bf.read(data);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    //接收文件路径的重载方法
    public static byte[] read(String bFile){
        return read(new File(bFile).getAbsoluteFile());
    }

    /**
     * 获取文件对象，不存在则创建
     * 当父文件夹不存在时，创建失败
     *
     * @param filePath
     * @return
     */
    public static File getFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 获取文件夹，不存在则创建
     *
     * @param folderPath
     * @return
     */
    public static void createFolder(String folderPath) {
        File file = new File(folderPath);
        //如果文件夹不存在则创建
        //file.exists(); 目录不存在时，自然file.isDirectory()==false
        if (!file.isDirectory()) {
            file.mkdir();
        }
    }


    /**
     * 写文件
     *
     * @param filePath
     * @param inputBytes
     * @param off        从inputBytes数组下标off处开始写
     * @param len        写len个字节长度
     * @param append     追加写 true，覆盖写 false
     * @throws IOException
     */
    public static void write(String filePath, byte[] inputBytes, int off, int len,
                             boolean append) {
        File file = getFile(filePath);
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            fos = new FileOutputStream(file, append);
            bos = new BufferedOutputStream(fos);
            bos.write(inputBytes, off, len);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭流
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 写入的重载方法   覆盖写
     * 整个byte数组全部写入
     *
     * @param filePath
     * @param inputBytes
     * @throws IOException
     */
    public static void write(String filePath, byte[] inputBytes){
        write(filePath, inputBytes, 0, inputBytes.length, false);
    }


    /**
     * 获取指定文件夹下文件列表
     *
     * @param folderPath
     * @return
     */
    public static ArrayList<File> getFileList(String folderPath) {
        ArrayList<File> fileList = new ArrayList<>();
        File folder = new File(folderPath);
        if (folder.isDirectory()) {
            File[] fileArr = folder.listFiles();
            for (int i = 0; i < fileArr.length; ++i) {
                File fileOne = fileArr[i];
                if (fileOne.isFile()) {
                    fileList.add(fileOne);
                }
            }
        }
        return fileList;
    }

    /**
     * 合并文件
     *
     * @param outFile 输出路径,含文件名
     * @param files   需要合并的文件路径 可是File[]，也可是String[]
     */
    private static final int BUFSIZE = 1024 * 8;

    public static void mergeFiles(String outFile, File[] files){
        FileChannel outChannel = null;
        try {
            outChannel = new FileOutputStream(outFile).getChannel();
            for (File f : files) {
                FileChannel fc = new FileInputStream(f).getChannel();
                ByteBuffer bb = ByteBuffer.allocate(BUFSIZE);
                while (fc.read(bb) != -1) {
                    bb.flip();
                    outChannel.write(bb);
                    bb.clear();
                }
                fc.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }finally {
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
