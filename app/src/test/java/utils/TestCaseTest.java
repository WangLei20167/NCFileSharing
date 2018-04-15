package utils;

import android.util.Log;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mroot on 2018/4/7.
 */
public class TestCaseTest {
    @Test
    public void test1() throws Exception {
        TestCase testCase = new TestCase();
        int result = testCase.test(5, 6);
        System.out.println("result=" + result);

        System.out.println(TestCase.Msg.TEST1.ordinal());
        System.out.println(TestCase.Msg.TEST2.ordinal());

        List<String> strings=new ArrayList<>();
        strings.add("string1");
        strings.add("string2");
        strings.add("string3");
        strings.add("string4");
        strings.add("string5");
        for (final String string : strings) {
            System.out.println(string);
        }

        //打印
//        string1
//        string2
//        string3
//        string4
//        string5

    }
}

