package com.huan.administrator.screensharingmobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {

    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(ACTION)) {
            Intent intent1 = new Intent(context, com.huan.administrator.screensharingmobile.FxService.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(intent1);
        }
    }
}