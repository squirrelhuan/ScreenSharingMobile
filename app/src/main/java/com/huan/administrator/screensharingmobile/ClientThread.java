package com.huan.administrator.screensharingmobile;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class ClientThread implements Runnable {
    private static Socket socket ;
    private static ByteArrayOutputStream outputstream;
    private static byte byteBuffer[] = new byte[1024];
    public static Size size;

    //向UI线程发送消息
    private Handler handler;

    //接受UI线程消息
    public MyHandler revHandler;

    BufferedReader br= null;
    static OutputStream os = null;

    public ClientThread(Handler handler){
        this.handler=handler;

    }

    @Override
    public void run() {


        Looper.prepare();
        //接受UI发来的信息
        revHandler = new MyHandler();
        Looper.loop();

    }

    public static class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            if(msg.what==0x111){
                try {
                    socket = new Socket("192.168.31.144",26891);
                    os = socket.getOutputStream();
                    YuvImage image = (YuvImage) msg.obj;
                    if(socket.isOutputShutdown()){
                        socket.getKeepAlive();

                    }else{
                        os = socket.getOutputStream();
                        outputstream = new ByteArrayOutputStream();
                        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, outputstream);
                        ByteArrayInputStream inputstream = new ByteArrayInputStream(outputstream.toByteArray());
                        int amount;
                        while ((amount = inputstream.read(byteBuffer)) != -1) {
                            os.write(byteBuffer, 0, amount);
                        }
                        os.write("\n".getBytes());
                        outputstream.flush();
                        outputstream.close();
                        os.flush();
                        os.close();
                        socket.close();
                    }
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}