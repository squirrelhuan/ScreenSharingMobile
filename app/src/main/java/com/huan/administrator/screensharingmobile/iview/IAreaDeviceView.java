package com.huan.administrator.screensharingmobile.iview;


import com.huan.administrator.screensharingmobile.bean.AreaDeviceBean;

/**
 * Created by user on 2017/5/9.
 */

public interface IAreaDeviceView {
    void upLocalData(AreaDeviceBean localBean);

    void upListData(boolean isChange, int numDevice);
}
