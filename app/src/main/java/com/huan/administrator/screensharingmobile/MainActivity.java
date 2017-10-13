package com.huan.administrator.screensharingmobile;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.huan.administrator.screensharingmobile.adapter.AreaDeviceAdapter;
import com.huan.administrator.screensharingmobile.base.BaseActivity;
import com.huan.administrator.screensharingmobile.bean.AreaDeviceBean;
import com.huan.administrator.screensharingmobile.iview.IAreaDeviceView;
import com.huan.administrator.screensharingmobile.present.AreaDevicePresent;

public class MainActivity extends BaseActivity implements View.OnClickListener,IAreaDeviceView {

    protected Context mContext;
    private static CheckBox rb_start_share;
    private ListView mListView;
    private AreaDeviceAdapter mAdapter;
    private AreaDevicePresent mPresent;

    private ImageView mLocalIv;
    private TextView mLocalNameTv;
    private TextView mLocalIpTv;
    private TextView mLocalMacTv;

    private TextView tv_device_title;
    @Override
    public void initView(){

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rb_start_share:
                startService(new Intent(this,FxService.class));
                //this.finish();
                break;
        }
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_main);
        mContext = this;

        if (Build.VERSION.SDK_INT >= 21) {
            mMediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        }
    }

    @Override
    public void findView() {
        rb_start_share = (CheckBox) findViewById(R.id.rb_start_share);
        mListView = (ListView) findViewById(R.id.activity_area_device_list);
        tv_device_title = (TextView) findViewById(R.id.tv_device_title);

        mLocalIv = (ImageView) findViewById(R.id.item_area_device_iv);
        mLocalNameTv = (TextView) findViewById(R.id.item_area_device_name);
        mLocalIpTv = (TextView) findViewById(R.id.item_area_device_ip);
        mLocalMacTv = (TextView) findViewById(R.id.item_area_device_mac);
    }

    Intent intent;
    @Override
    public void initData() {
        mPresent = new AreaDevicePresent(this, mContext);
        mAdapter = new AreaDeviceAdapter(mContext, mPresent.getData());
        mListView.setAdapter(mAdapter);

        intent = new Intent(MainActivity.this,FxService.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FxService.mainActivity = this;
        if(areaDeviceBean!=null){
        tv_device_title.setText("分享到设备："+areaDeviceBean.getIp());
            rb_start_share.setSelected(true);
        }
        if(FxService.canSendImage){
                rb_start_share.setBackgroundResource(R.drawable.btn_circle_02);
                rb_start_share.setText("正在共享");
            }else{
                rb_start_share.setBackgroundResource(R.drawable.btn_circle_green);
                rb_start_share.setText("开始共享");
        }
    }

    static AreaDeviceBean areaDeviceBean;
    static String deviceIp;
    @Override
    public void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                areaDeviceBean = mPresent.getData().get(i);

                tv_device_title.setText("分享到设备："+areaDeviceBean.getIp());
                FxService.setIp(areaDeviceBean.getIp());
            }
        });
        rb_start_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceIp = tv_device_title.getText().toString();
                if(deviceIp.trim().equalsIgnoreCase("暂无设备")){
                    Toast.makeText(MainActivity.this,"暂无设备,请选择设备后再操作！",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!FxService.canSendImage){
                    rb_start_share.setBackgroundResource(R.drawable.btn_circle_02);
                    rb_start_share.setText("正在共享");

                    permission();
                    FxService.setCanSendImage(true);
                    startIntent();
                    //startService(intent);
                }else{
                    FxService.setCanSendImage(false);
                    rb_start_share.setBackgroundResource(R.drawable.btn_circle_green);
                    rb_start_share.setText("开始共享");
                    // 停止指定Service
                    stopService(intent);
                }
            }
        });
    }

    @Override
    public void upLocalData(AreaDeviceBean localBean) {
        mLocalIv.setImageResource(localBean.getResId());
        mLocalNameTv.setText(localBean.getName());
        mLocalIpTv.setText(localBean.getIp());
        mLocalMacTv.setText(localBean.getMac());
    }

    @Override
    public void upListData(boolean isChange, int numDevice) {
        if (isChange && mAdapter != null) {//避免异常
            mAdapter.notifyDataSetChanged();
        }
    }


    private String TAG = "Service";
    private int result = 0;
    private MediaProjectionManager mMediaProjectionManager;
    private int REQUEST_MEDIA_PROJECTION = 1;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startIntent(){
        if(intent != null && result != 0){
            Log.i(TAG, "user agree the application to capture screen");
            //Service1.mResultCode = resultCode;
            //Service1.mResultData = data;
            ((ShotApplication)getApplication()).setResult(result);
            ((ShotApplication)getApplication()).setIntent(intent);
            Intent intent = new Intent(getApplicationContext(), FxService.class);
            startService(intent);
            Log.i(TAG, "start service Service1");
        }else{
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
            //Service1.mMediaProjectionManager1 = mMediaProjectionManager;
            ((ShotApplication)getApplication()).setMediaProjectionManager(mMediaProjectionManager);
        }
    }

    public void permission(){
        if (Build.VERSION.SDK_INT >= 23) {
                if (! Settings.canDrawOverlays(MainActivity.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 10);
                }
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 10) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    // SYSTEM_ALERT_WINDOW permission not granted...
                    Toast.makeText(MainActivity.this,"not granted",Toast.LENGTH_SHORT);
                }
            }
        }

        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }else if(data != null && resultCode != 0){
                Log.i(TAG, "user agree the application to capture screen");
                //Service1.mResultCode = resultCode;
                //Service1.mResultData = data;
                result = resultCode;
                intent = data;
                ((ShotApplication)getApplication()).setResult(resultCode);
                ((ShotApplication)getApplication()).setIntent(data);
                Intent intent = new Intent(getApplicationContext(), FxService.class);
                startService(intent);
                Log.i(TAG, "start service Service1");

                finish();
            }
        }
    }

    public void setImageButton(){
        if(FxService.canSendImage){
            rb_start_share.setBackgroundResource(R.drawable.btn_circle_02);
            rb_start_share.setText("正在共享");
        }else{
            rb_start_share.setBackgroundResource(R.drawable.btn_circle_green);
            rb_start_share.setText("开始共享");
        }
    }
}
