package connect;

/**
 * Created by mroot on 2018/4/15.
 */

public class SMsgType {
    //约定好的信息类型值
    //1 表示是XML文件
    //2 表示是编码文件
    //3 表示是文件请求
    //4 表示已经不再需要信息（可以断开了）

    //isFile
    public static final int TYPE_XML = 296;
    public static final int TYPE_EFILE = 781;
    public static final int TYPE_REQUEST = 493;  //文件请求需要发送字节，也作为特殊文件

    public static final int TYPE_CLIENT_CONNECT = 531;
}
