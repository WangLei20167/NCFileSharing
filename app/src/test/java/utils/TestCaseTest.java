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
    }

}