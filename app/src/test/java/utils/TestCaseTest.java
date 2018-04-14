package utils;

import org.junit.Test;

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

        int a = 3;
        String strTest=null;
        switch (a) {
            case 0:
                break;
            default:
                strTest = "this is a switch test!";
            case 1:
                strTest = "test!";
                break;
            case 3:
                break;
        }
        System.out.println(strTest);
    }
}

