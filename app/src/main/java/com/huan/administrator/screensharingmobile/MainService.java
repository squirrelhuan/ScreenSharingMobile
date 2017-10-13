package com.huan.administrator.screensharingmobile;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/5/18.
 */

public class MainService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("test","start service");
        Toast.makeText(getApplicationContext(),"start",Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
