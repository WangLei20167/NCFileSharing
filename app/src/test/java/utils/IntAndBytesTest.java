package utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by mroot on 2018/4/14.
 */
public class IntAndBytesTest {
    @Test
    public void byte2Arr_int2Arr() throws Exception {

        byte[][] bytes = {{23, -19}, {0, 111}, {-111, 18}};
        int[][] intArray = IntAndBytes.byte2Arr_int2Arr(bytes);
    }

}