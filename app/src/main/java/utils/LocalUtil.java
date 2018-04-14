package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

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
}
