package com.huan.administrator.screensharingmobile.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {

    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView();
        findView();
        initView();
        initListener();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public abstract void setContentView();

    public abstract void findView();

    public abstract void initView();

    public abstract void initData();

    public abstract void initListener();


}
