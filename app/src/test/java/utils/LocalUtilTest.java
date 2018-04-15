package utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by mroot on 2018/4/15.
 */
public class LocalUtilTest {
    @Test
    public void getRandomString() throws Exception {

        String str=LocalUtil.getRandomString(5);
        String string=LocalUtil.getRandomString(5);
        String string1=LocalUtil.getRandomString(5);
        String string2=LocalUtil.getRandomString(5);
        String string3=LocalUtil.getRandomString(5);
        String string4=LocalUtil.getRandomString(5);
    }

}