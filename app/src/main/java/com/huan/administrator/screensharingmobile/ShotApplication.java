package com.huan.administrator.screensharingmobile;

import android.app.Application;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;

import com.huan.mylog.MyLog;

/**
 * Created by Dylan_Wang on 2015/7/28.
 */
public class ShotApplication extends Application {
    private int result;
    private Intent intent;
    private MediaProjectionManager mMediaProjectionManager;


    public static UdpService udpService;

    public int getResult(){
        return result;
    }

    public Intent getIntent(){
        return intent;
    }

    public MediaProjectionManager getMediaProjectionManager(){
        return mMediaProjectionManager;
    }

    public void setResult(int result1){
        this.result = result1;
    }

    public void setIntent(Intent intent1){
        this.intent = intent1;
    }

    public void setMediaProjectionManager(MediaProjectionManager mMediaProjectionManager){
        this.mMediaProjectionManager = mMediaProjectionManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyLog myLog = new MyLog(this);
        myLog.initialization();
        myLog.setPrintType(MyLog.PrintType.All);// 设置打印类型
        myLog.setErrorToast("对不起程序崩溃了");// 设置崩溃提示
        // mylog.setErrorCatchedListener(new one);
    }
}
