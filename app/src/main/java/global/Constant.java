package global;

import android.os.Environment;

import utils.FileUtil;

/**
 * Created by mroot on 2018/4/8.
 */

public final class Constant {
    //作为Server时的监听地址与端口
    public final static String ServerIP = "192.168.43.1";
    public final static int ServerPORT = 9000;
    //测试标志
    public static final String TEST_FLAG = "hanhai";
    public static String DATA_FOLDER_PATH;
    public static String DATA_TEMP_PATH;
    public static String CRASH_PATH;

    static {
        DATA_FOLDER_PATH = Environment.getExternalStorageDirectory().getPath() + "/1FileSharing_NC";
        //创建app储存主目录
        FileUtil.createFolder(DATA_FOLDER_PATH);
        //文件初始化处理时的缓存目录
        DATA_TEMP_PATH = DATA_FOLDER_PATH + "/Temp";
        FileUtil.createFolder(DATA_TEMP_PATH);
        CRASH_PATH = DATA_FOLDER_PATH + "/crash";
        FileUtil.createFolder(CRASH_PATH);
    }


}
