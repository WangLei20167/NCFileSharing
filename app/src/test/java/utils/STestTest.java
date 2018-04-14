package utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by mroot on 2018/4/13.
 */
public class STestTest {
    @Test
    public void getTestInt() throws Exception {
        STest sTest=STest.getInstance();
        System.out.println(sTest.getTestInt());
        sTest.setTestInt(888);
        System.out.println(sTest.getTestInt());
    }

}