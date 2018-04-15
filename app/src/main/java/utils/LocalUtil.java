package utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by mroot on 2018/4/11.
 */

public class LocalUtil {
    /**
     * 获取系统时间
     * @param dataFormat 时间的格式
     * @return
     */
    public static String getTime(String dataFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dataFormat);//"yyyy-MM-dd HH:mm:ss:SSS"
        Date curDate = new Date();
        return format.format(curDate);
    }
    /**
     * 随机字符标志
     */
    public static String getRandomString(int length) {
        StringBuffer buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        int range = buffer.length();
        for (int i = 0; i < length; i ++) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }
        return sb.toString();
    }

}
