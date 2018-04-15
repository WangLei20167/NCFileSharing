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

    /**
     * 给byte数组增加一个长度，并赋值
     * @param originArray
     * @param value
     * @return
     */
    public static byte[] byteArrayGrow(byte[] originArray, byte value) {
        int col = originArray.length;
        byte[] newArray = new byte[col + 1];
        System.arraycopy(originArray, 0, newArray, 0, col);
        newArray[col]=value;
        return newArray;
    }
}
