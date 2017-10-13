package com.huan.administrator.screensharingmobile.uitls;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;


import com.huan.administrator.screensharingmobile.bean.AreaDeviceBean;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017/5/11.
 */

public class AllUitls {
    public static final String ARP_FILE_NAME = "/proc/net/arp";

    /**
     * 读取局域网在线设备
     *
     * @param localIp
     * @return
     */
    public static List<AreaDeviceBean> getAllCacheMac(String localIp) {
        List<AreaDeviceBean> data = new ArrayList<>();
        AreaDeviceBean bean;
        BufferedReader br = null;
        String[] prefixArr = localIp.split("\\.");
        String routIp = null;
        if (prefixArr.length > 2) {
            routIp = prefixArr[0] + "." + prefixArr[1] + "." + prefixArr[2] + "." + 1;
        }
        try {
            br = new BufferedReader(new FileReader(ARP_FILE_NAME));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted.length >= 4) {
                    String ipCache = splitted[0];
                    if (!TextUtils.isEmpty(ipCache)
                            && !ipCache.equals(localIp)//本机
                            && !ipCache.equals(routIp)) {// 192.168.1.1
                        String mac = splitted[3];
                        if (mac.matches("..:..:..:..:..:..") && !mac.equals("00:00:00:00:00:00")) {
                            bean = new AreaDeviceBean();
                            bean.setIp(ipCache);
                            bean.setMac(mac);
                            data.add(bean);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }


    /**
     * 初始手机本地文件的ip缓存表
     *
     * @param context
     */
    public static void initAreaIp(Context context) {
        String localIp = getIPAddressStr(context);
        String[] prefixArr = localIp.split("\\.");
        String prefixStr;
        if (prefixArr.length > 2) {
            prefixStr = prefixArr[0] + "." + prefixArr[1] + "." + prefixArr[2] + ".";
            try {
                byte[] b = new byte[0];
                DatagramPacket dp = new DatagramPacket(b, 0, 0);
                DatagramSocket socket = new DatagramSocket();
                int position = 2;
                while (position < 255) {
                    dp.setAddress(InetAddress.getByName(prefixStr + String.valueOf(position)));
                    socket.send(dp);
                    position++;
                    if (position == 125) {//分两段掉包，一次性掉的话，达到236左右，会耗时3秒左右再往下掉
                        socket.close();
                        socket = new DatagramSocket();
                    }
                }
                socket.close();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    private static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    /**
     * 获取Ip地址，返回string类型：192.068.1.1
     *
     * @param context
     * @return
     */
    public static String getIPAddressStr(Context context) {

        int ipAddress = getIPAddress(context);
        if (ipAddress == 0) {
            return "";
        } else {
            return int2ip(ipAddress);
        }
    }

    /**
     * 获取Ip地址，返回int类型
     *
     * @param context
     * @return
     */
    private static int getIPAddress(Context context) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            return wifiInfo.getIpAddress();
        }
        return 0;
    }


    /**
     * 获取本机mac
     * @return
     */
    public static String getLocalMac() {
        String mac = null;
        String str;
        try {
            BufferedReader br = new BufferedReader(new FileReader("/sys/class/net/wlan0/address"));
            while ((str = br.readLine()) != null) {
                mac = str.trim();
            }
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return mac;
    }
}
