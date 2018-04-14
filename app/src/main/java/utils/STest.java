package utils;

/**
 * Created by mroot on 2018/4/13.
 */

public class STest {
    private static final STest ourInstance = new STest();

    public static STest getInstance() {
        return ourInstance;
    }

    private int testInt=9;
    private STest() {
    }

    public int getTestInt() {
        return testInt;
    }

    public void setTestInt(int testInt) {
        this.testInt = testInt;
    }
}
