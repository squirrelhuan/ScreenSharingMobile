package com.huan.administrator.screensharingmobile.bean;


import com.huan.administrator.screensharingmobile.R;

/**
 * Created by user on 2017/5/9.
 * 局域网设备
 */

public class AreaDeviceBean {
    private int resId = R.drawable.icon_share_qq_normal;
    private String name = "unknow";
    private String ip;
    private String mac;

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
