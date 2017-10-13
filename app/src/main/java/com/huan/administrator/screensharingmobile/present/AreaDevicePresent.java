package com.huan.administrator.screensharingmobile.present;

import android.content.Context;
import android.os.Build;
import android.os.SystemClock;


import com.huan.administrator.screensharingmobile.bean.AreaDeviceBean;
import com.huan.administrator.screensharingmobile.iview.IAreaDeviceView;
import com.huan.administrator.screensharingmobile.uitls.AllUitls;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by user on 2017/5/9.
 */

public class AreaDevicePresent {
    private IAreaDeviceView mIView;
    private Context mContext;
    private List<AreaDeviceBean> mData;
    private String mLocalIP;
    private Subscriber<List> mSubscriber;

    public AreaDevicePresent(IAreaDeviceView iView, Context context) {
        this.mIView = iView;
        this.mContext = context;
        initData();
    }

    public List<AreaDeviceBean> getData() {
        return mData;
    }

    private void initData() {
        mLocalIP = AllUitls.getIPAddressStr(mContext);
        mData = new ArrayList<>();
        mSubscriber = new Subscriber<List>() {
            @Override
            public void onCompleted() {
                mIView.upListData(true, mData.size() + 1);//加上本机
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List list) {
                mData.clear();
                mData.addAll(list);
            }
        };
        initLocalDevice();
        initOtherDevice();
    }

    private void initLocalDevice() {
        AreaDeviceBean bean = new AreaDeviceBean();
        bean.setName(Build.MODEL + "(我的设备)");
        bean.setIp(mLocalIP);
        bean.setMac(AllUitls.getLocalMac());
        mIView.upLocalData(bean);
    }

    private void initOtherDevice() {
        Observable.create(new Observable.OnSubscribe<List>() {
            @Override
            public void call(Subscriber<? super List> subscriber) {
                AllUitls.initAreaIp(mContext);
                List<AreaDeviceBean> beans = new ArrayList<>();
                int sum = 0;
                while (beans.size() == 0 && sum < 8) {
                    beans.addAll(AllUitls.getAllCacheMac(mLocalIP));
                    SystemClock.sleep(beans.size()>0?0:1000);
                    sum++;
                }
                subscriber.onNext(beans);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mSubscriber);
    }

    public void onDestroy() {
        if (!mSubscriber.isUnsubscribed()) {
            mSubscriber.unsubscribe();
        }
    }
}
