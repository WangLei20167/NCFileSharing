package com.example.mroot.ncfilesharing;

import android.app.Activity;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mauiie.aech.AECHConfiguration;
import com.mauiie.aech.AECrashHelper;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bufferfile.EncodeFile;
import bufferfile.SolveSocketMsg;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.SocketMessage;
import connect.TCPClient;
import connect.TCPServer;
import global.Constant;
import global.MsgValue;
import nc.NCUtils;
import runtimepermissions.PermissionsManager;
import runtimepermissions.PermissionsResultAction;
import wifi.APHelper;
import wifi.WifiAdmin;

public class MainActivity extends AppCompatActivity {
    private static Context context;

    @BindView(R.id.tv)
    TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ButterKnife注册
        ButterKnife.bind(this);
        //权限匹配
        requestPermission();
        context = MainActivity.this;
        //全局抓取异常   世界警察
        //并打印异常日志
        AECrashHelper.initCrashHandler(getApplication(),
                new AECHConfiguration.Builder()
                        .setLocalFolderPath(Constant.CRASH_PATH) //配置日志信息存储的路径
                        .setSaveToLocal(true).build()); //开启存储在本地功能

        tv.setText("hello tom");

    }


    public void onTest(View view) {
        //TCPClient tcpClient = TCPClient.getInstance();
        //APHelper apHelper = APHelper.getInstance();
        //apHelper.openAP();
        //tcpClient.sendFile(new File("/storage/emulated/0/1NCSharing/Log/sendLog.txt"));

        EncodeFile encodeFile = EncodeFile.getInstance();
        //encodeFile.init("/storage/emulated/0/1NCSharing/Log/testBytes.txt", 4);
        encodeFile.init(Constant.DATA_TEMP_PATH + "/" + "testBytes" + "/testBytes.txt.txt");
        String fileName = encodeFile.getFileName();
    }

    public void onTest1(View view) {
        String str = "ABCDEFGH";
        //FileUtil.write("/storage/emulated/0/1NCSharing/Never Say Never.mp4", str.getBytes());

        //Log.i(Constant.TEST_FLAG, Constant.DATA_FOLDER_PATH);
        EncodeFile encodeFile = EncodeFile.getInstance();
        //encodeFile.init("/storage/emulated/0/1NCSharing/Log/testBytes.txt", 4);
        encodeFile.init("/storage/emulated/0/1NCSharing/Never Say Never.mp4", 6);
        //encodeFile.init("/storage/emulated/0/1NCSharing/Log/sendLog.txt", 4);
        encodeFile.recoverFile();
    }

    @OnClick(R.id.btn_openServer)
    void openServer() {
        TCPServer tcpServer = TCPServer.getInstance();
        tcpServer.openServer(Constant.ServerPORT);
    }

    @OnClick(R.id.btn_connect)
    void connectServer() {
        TCPClient tcpClient = TCPClient.getInstance();
        tcpClient.connect(Constant.ServerIP, Constant.ServerPORT);
    }


    //选择文件
    public void selectFile() {
        //打开文件选择器
        Intent i = new Intent(this, FilePickerActivity.class);
        //单选
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        //多选
        //i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        //设置开始时的路径
        //i.putExtra(FilePickerActivity.EXTRA_START_PATH, startPath);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        startActivityForResult(i, FILE_CODE);
    }

    private static final int FILE_CODE = 1;

    //得到文件选择的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //取到选择文件的地址
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            List<Uri> files = Utils.getSelectedFilesFromResult(data);
            List<File> fileList = new ArrayList<>();
            for (Uri uri : files) {
                File file = Utils.getFileForUri(uri);
                // Do something with the result...
                fileList.add(file);
                Log.i(Constant.TEST_FLAG, file.getPath());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //处理各个线程发来的消息
    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MsgValue.SHOW_MSG:
                    Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;

                case MsgValue.SOCKET_MSG:
                    SolveSocketMsg solveSocketMsg = SolveSocketMsg.getInstance();
                    solveSocketMsg.dealMsgEvent((SocketMessage) msg.obj);
                    break;

                case MsgValue.SOLVE_RESPONSE:
                    TCPServer tcpServer = TCPServer.getInstance();
                    tcpServer.sendCode(((SocketMessage) msg.obj).getSocket(), 1);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //点击两次back退出程序
    private long mExitTime;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            //执行退出操作,并释放资源
            APHelper.getInstance().closeAP();
            WifiAdmin.getInstance().closeWifi();
            NCUtils.UninitGalois();
            finish();
            //Dalvik VM的本地方法完全退出app
            Process.killProcess(Process.myPid());    //获取PID
            System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
        }
    }

    //返回context对象
    public static Context getContextObject() {
        return context;
    }


    /**
     * 适配android6.0以上权限                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         =
     */
    private void requestPermission() {
        /**
         * 请求所有必要的权限
         */
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                //1、获取IMEI的权限
                //2、访问文件的权限
                //3、(wifi扫描)需要的手机定位权限
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
                //Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
