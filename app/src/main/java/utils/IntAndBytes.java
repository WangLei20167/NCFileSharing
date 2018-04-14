package utils;

/**
 * Created by mroot on 2018/4/14.
 */

public class IntAndBytes {

    //byte数组转化为int数组
    //全是非负数
    public static int[][] byte2Arr_int2Arr(byte[][] byte2Arr) {
        int row = byte2Arr.length;
        int col = byte2Arr[0].length;
        int[][] result = new int[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                int temp = (int) byte2Arr[i][j];
                if (temp >= 0) {
                    result[i][j] = temp;
                } else {
                    result[i][j] = 256 + temp;
                }
            }
        }
        return result;
    }
}
