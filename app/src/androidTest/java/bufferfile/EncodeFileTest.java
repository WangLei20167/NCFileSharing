package bufferfile;

import org.junit.Test;

import utils.FileUtil;

/**
 * Created by mroot on 2018/4/11.
 */
public class EncodeFileTest {
    @Test
    public void init() throws Exception {
//        byte[] testBytes=new byte[20];
//        for (int i = 1; i < 21; i++) {
//            testBytes[i]=(byte) i;
//        }
        //String str="ABCDEFG";
        //FileUtil.write("/storage/emulated/0/1NCSharing/Log/testBytes.txt",str.getBytes());


        //Log.i(Constant.TEST_FLAG, Constant.DATA_FOLDER_PATH);
//        EncodeFile encodeFile = EncodeFile.getInstance();
//        encodeFile.init("/storage/emulated/0/1NCSharing/Log/testBytes.txt", 4);
//        //encodeFile.init("/storage/emulated/0/1NCSharing/Never Say Never.mp4", 4);
//        encodeFile.recoverFile();

        EncodeFile encodeFile=EncodeFile.getInstance();
        encodeFile.init("/storage/emulated/0/1NCSharing/Log/testBytes.txt", 4);
       // EncodeFile file=EncodeFile.xml2obj()
    }

}