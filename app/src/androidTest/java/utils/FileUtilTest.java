package utils;

import android.util.Log;

import org.junit.Test;

import global.Constant;

/**
 * Created by mroot on 2018/4/11.
 */
public class FileUtilTest {
    @Test
    public void createFolder() throws Exception {

        FileUtil.createFolder("/storage/emulated/0/1RSSharing/folderTest");
    }

    @Test
    public void getFile() throws Exception {

        FileUtil.getFile("/storage/emulated/0/1RSSharing/myFileTest.txt");
    }

    @Test
    public void write() throws Exception {
        String str = "this  is a android 写文件测试";
        FileUtil.write("/storage/emulated/0/1RSSharing/myWrite.txt", str.getBytes());
        Log.i(Constant.TEST_FLAG, str);
    }

    @Test
    public void read() throws Exception {
        byte[] bytes = FileUtil.read("/storage/emulated/0/1RSSharing/Log/revLog.txt");
        String s = new String(bytes);
        Log.i(Constant.TEST_FLAG, s);
    }

}