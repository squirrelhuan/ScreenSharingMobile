package com.huan.administrator.screensharingmobile;


import android.annotation.TargetApi;
import android.app.Instrumentation;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.huan.administrator.screensharingmobile.bean.UdpData;
import com.huan.administrator.screensharingmobile.bean.User;
import com.huan.administrator.screensharingmobile.uitls.FormatTools;
import com.huan.administrator.screensharingmobile.uitls.ImageUtils;
import com.huan.mylog.MyLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;


public class FxService extends Service implements OnTouchListener, OnClickListener, OnLongClickListener {

    //���帡�����ڲ���
    LinearLayout mFloatLayout;
    LayoutParams wmParams;
    // WindowManager.LayoutParams wmParams;
    WindowManager mWindowManager;
    static ImageView mFloatView, mFloatView1, mFloatView2, mFloatView3, mFloatView4;

    private static final String TAG = "FxService";
    private static final int toleft = 1;
    private static final int toright = 2;
    private int height;
    private int xlocation, ylocation;
    private mHandler handle;
    private int width;
    private PopupWindow pW = null;
    private static Context mContext = null;
    public static MainActivity mainActivity = null;

    public static String ip = "192.168.31.144";
    public static boolean canSendImage = false;

    public static boolean isCanSendImage() {
        return canSendImage;
    }

    public static void setCanSendImage(boolean canSendImage) {
        FxService.canSendImage = canSendImage;
        if(mFloatView!=null&&mContext!=null){
            if (!canSendImage) {
                mFloatView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_float_play));
                //Toast.makeText(FxService.this, "屏幕分享已暂停", Toast.LENGTH_SHORT).show();
            } else {
                mFloatView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_float_pause));
               // Toast.makeText(FxService.this, "屏幕分享已开始", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void setIp(String ip) {
       FxService.ip = ip;
        //ip = "192.168.31.144";
        //FxService.ip = "192.168.31.246";
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        width = dm.widthPixels;
        super.onCreate();
        MyLog.i(TAG, "oncreat");
        setCanSendImage(true);
        mContext = this;
        //com.example.mycloud.MainActivity.width;
        //width=intentdata.getIntent(intentdata);

        handle = new mHandler();
        createFloatView();
        //Toast.makeText(FxService.this, "create FxService", Toast.LENGTH_LONG);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (mFloatView.getVisibility() == View.VISIBLE) {
                    mFloatView.setAlpha(130);
                }
            }
        }, 5000);

        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        wmParams.type = LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        initDate();
        try {
            if (client == null) {
                client = new DatagramSocket();
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        //MyLog.i(TAG, "created the float sphere view");
    }
    private void initDate(){
        createVirtualEnvironment();
        runnable = new Runnable() {
            @Override
            public void run() {
                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    public void run() {
                        //start virtual
                        startVirtual();
                    }
                }, 0);

                Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    public void run() {
                        //capture theBufferQueue has been abandoned screen
                        startCapture();
                    }
                }, 0);
            }
        };
        handler.postDelayed(runnable, 0);
    }

    // 发送图片线程类
    class Image_Thread implements Runnable {
        Bitmap bitmap;

        Image_Thread(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        public void run() {
           // while (true) {
                try {
                  // Thread.sleep(10);

                    int port = 26891;
                    InetAddress addr = InetAddress
                            .getByName(ip);
                    sendImage2(this.bitmap, addr, port);
                } /*catch (InterruptedException e) {
                    e.printStackTrace();
                    MyLog.a(e.getMessage());
                }*/ catch (UnknownHostException e) {
                    e.printStackTrace();
                    MyLog.a(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    MyLog.a(e.getMessage());
                }finally {
                    //MyLog.i("准备再次开始");
                    startCapture();
                   // initDate();
                }
        }
    }

    // 心跳线程类
    class Heartbeat_Thread implements Runnable {
        String path;

        Heartbeat_Thread(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(200);

                    int port = 26891;
                    InetAddress addr = InetAddress
                            .getByName(ip);
                    sendImage(this.path, addr, port);
                } catch (InterruptedException e) {
                    MyLog.a(e.getMessage());
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    MyLog.a(e.getMessage());
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                    MyLog.a(e.getMessage());
                } finally {
                }

                MyLog.i(TAG, "ServiceDemo is running...");
            }
        }
    }

    private DatagramPacket datagramPacket;
    private DatagramSocket datagramSocket;
    byte b[] = new byte[5120];
    private FileInputStream in;

    /**
     * 图片转换为文件流分片并发送
     *
     * @Author ccj
     */
    public void sendImage(String path, InetAddress ip, int port) throws FileNotFoundException {

        MyLog.i("-->发送图片开始");
        try {
            in = new FileInputStream(path);
            int n = -1;
            while ((n = in.read(b)) != -1) {
                in.hashCode();
                datagramPacket = new DatagramPacket(b, b.length, ip, port);
                datagramSocket = new DatagramSocket();
                datagramSocket.send(datagramPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
            MyLog.a(e.getMessage());
        }
        if (datagramSocket != null)
            datagramSocket.close();
        String end = ";!";
        datagramPacket = new DatagramPacket(end.getBytes(), end.getBytes().length, ip, port);
        try {
            datagramSocket = new DatagramSocket();
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
            MyLog.a(e.getMessage());
        }
        MyLog.i(ip + ";" + port);
        datagramSocket.close();
        MyLog.i("发送图片完成");

        handler.postDelayed(runnable, 50);
    }

    /**
     * 图片转换为文件流分片并发送
     *
     * @Author ccj
     */
    public void sendImage2(Bitmap bitmap, InetAddress ip, int port) throws FileNotFoundException {

        MyLog.i("-->发送图片开始");
        if(bitmap!=null && canSendImage) {
            try {

			/*ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte data2[] = baos.toByteArray();*/

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap_c = ImageUtils.compressImage(bitmap);
                bitmap_c.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                MyLog.d("图片大小："+baos.toByteArray().length/1024+"K");
                InputStream in = new ByteArrayInputStream(baos.toByteArray());
                //in = new FileInputStream(path);
                int n = -1;
                while ((n = in.read(b)) != -1) {
                    in.hashCode();
                    datagramPacket = new DatagramPacket(b, b.length, ip, port);
                    datagramSocket = new DatagramSocket();
                    datagramSocket.send(datagramPacket);
                }
                baos.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
                MyLog.a(e.getMessage());
            }
            //MyMyLog.d(new String(b));
            if (datagramSocket != null)
                datagramSocket.close();
            String end = ";!";
            datagramPacket = new DatagramPacket(end.getBytes(), end.getBytes().length, ip, port);
            try {
                datagramSocket = new DatagramSocket();
                datagramSocket.send(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
                MyLog.a(e.getMessage());
            }
            MyLog.i(ip + ";" + port);
            datagramSocket.close();
        }
        //MyLog.i("-->发送图片结束");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    class mHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case toleft:
                    wmParams.x -= 30;
                    if (wmParams.x >= 0) {
                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                        handle.sendEmptyMessageDelayed(toleft, 1);
                    } else {
                        wmParams.x = 0;
                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                    }
                    break;
                case toright:
                    wmParams.x += 30;
                    if (wmParams.x <= width) {
                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                        handle.sendEmptyMessageDelayed(toright, 1);
                    } else {
                        wmParams.x = width;
                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void createFloatView() {
        wmParams = new LayoutParams();
        //��ȡWindowManagerImpl.CompatModeWrapper

        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        //����window type
        wmParams.type = LayoutParams.TYPE_PHONE;
        //����ͼƬ��ʽ��Ч��Ϊ����͸��
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags =
//          LayoutParams.FLAG_NOT_TOUCH_MODAL |
                LayoutParams.FLAG_NOT_FOCUSABLE;
//          LayoutParams.FLAG_NOT_TOUCHABLE
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;

        wmParams.x = width;
        wmParams.y = 200;

        wmParams.width = 200;
        wmParams.height = 80;

        wmParams.width = LayoutParams.WRAP_CONTENT;
        wmParams.height = LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //��ȡ����������ͼ���ڲ���
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        //���mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);

        MyLog.i(TAG, "mFloatLayout-->left" + mFloatLayout.getLeft());
        MyLog.i(TAG, "mFloatLayout-->right" + mFloatLayout.getRight());
        MyLog.i(TAG, "mFloatLayout-->top" + mFloatLayout.getTop());
        MyLog.i(TAG, "mFloatLayout-->bottom" + mFloatLayout.getBottom());

        mFloatView = (ImageView) mFloatLayout.findViewById(R.id.float_id);
        mFloatView1 = (ImageView) mFloatLayout.findViewById(R.id.float_back_id);
        mFloatView2 = (ImageView) mFloatLayout.findViewById(R.id.float_home_id);
        mFloatView3 = (ImageView) mFloatLayout.findViewById(R.id.float_menu_id);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        MyLog.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth() / 2);
        MyLog.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight() / 2);
        mFloatView.setOnLongClickListener(this);
        ;

        mFloatView.setOnTouchListener(this);
        mFloatView1.setOnTouchListener(this);
        mFloatView2.setOnTouchListener(this);
        mFloatView3.setOnTouchListener(this);

        mFloatView1.setOnClickListener(this);
        mFloatView2.setOnClickListener(this);
        mFloatView3.setOnClickListener(this);
        mFloatView.setOnClickListener(this);
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        mFloatView.setAlpha(255);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (mFloatView.getVisibility() == View.VISIBLE) {
                    mFloatView.setAlpha(130);
                }
            }
        }, 5000);
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // TODO Auto-generated method stub
                xlocation = (int) event.getRawX();
                ylocation = (int) event.getRawY();
                wmParams.x = xlocation - mFloatLayout.getWidth() / 2;
                //25为状态栏高度
                wmParams.y = ylocation - mFloatLayout.getHeight() / 2 - 40;
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                break;
            case MotionEvent.ACTION_UP:
                if (xlocation > width / 2) {
                    handle.sendEmptyMessage(toright);
                } else {
                    handle.sendEmptyMessage(toleft);
                }
                break;
            default:
                break;

        }
        return false;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.float_id || v.getId() == R.id.float_back_id
                || v.getId() == R.id.float_home_id || v.getId() == R.id.float_menu_id) {
            mFloatView.setAlpha(255);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (mFloatView.getVisibility() == View.VISIBLE) {
                        mFloatView.setAlpha(130);
                    }
                }
            }, 5000);
            if (canSendImage/*mFloatView.getVisibility() == View.VISIBLE*/) {
                canSendImage = false;
                mFloatView.setImageDrawable(getResources().getDrawable(R.drawable.ic_float_play));
                Toast.makeText(FxService.this, "屏幕分享已暂停", Toast.LENGTH_SHORT).show();

                if(mainActivity!=null){
                    mainActivity.setImageButton();
                }
              /*  mFloatView1.setVisibility(View.VISIBLE);
                mFloatView2.setVisibility(View.VISIBLE);
                mFloatView3.setVisibility(View.VISIBLE);
                mFloatView.setVisibility(View.GONE);*/
            } else {
                canSendImage = true;
                mFloatView.setImageDrawable(getResources().getDrawable(R.drawable.ic_float_pause));
                Toast.makeText(FxService.this, "屏幕分享已开始", Toast.LENGTH_SHORT).show();
               /* mFloatView1.setVisibility(View.GONE);
                mFloatView2.setVisibility(View.GONE);
                mFloatView3.setVisibility(View.GONE);
                mFloatView.setVisibility(View.VISIBLE);*/
            }
        }

    }


    private MediaProjection mMediaProjection = null;
    Handler handler = new Handler();
    Runnable runnable;


    private static DatagramSocket client;
    private static Thread heartbeat_thread, image_thread;

    public void SendHeartMessage(String ImagePath, Bitmap bitmap) {

        if (bitmap != null) {
            try {
                File fileImage = new File(ImagePath);
                if (!fileImage.exists()) {
                    fileImage.createNewFile();
                } else {
                    fileImage.delete();
                    fileImage.createNewFile();
                }
                FileOutputStream out = new FileOutputStream(fileImage);
                if (out != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 20, out);

                    out.flush();
                    out.close();
                    //SendImage(bitmap);
                    //Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    //Uri contentUri = Uri.fromFile(fileImage);
                    //media.setData(contentUri);
                    //this.sendBroadcast(media);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                MyLog.a(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                MyLog.a(e.getMessage());
            }
        }

        if (heartbeat_thread == null) {
            heartbeat_thread = new Thread(new Heartbeat_Thread(ImagePath));
            heartbeat_thread.start();
        }
    }

    public void SendImage(Bitmap bitmap) {
            image_thread = new Thread(new Image_Thread(bitmap));
            image_thread.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startVirtual() {
        if (mMediaProjection != null) {
            MyLog.i(TAG, "want to display virtual");
            virtualDisplay();
        } else {
            MyLog.i(TAG, "start screen capture intent");
            MyLog.i(TAG, "want to build mediaprojection and display virtual");
            setUpMediaProjection();
            virtualDisplay();
        }
    }

    public static Intent mResultData = null;
    public static int mResultCode = 0;
    public static MediaProjectionManager mMediaProjectionManager1 = null;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setUpMediaProjection() {
        mResultData = ((ShotApplication) getApplication()).getIntent();
        mResultCode = ((ShotApplication) getApplication()).getResult();
        mMediaProjectionManager1 = ((ShotApplication) getApplication()).getMediaProjectionManager();
        mMediaProjection = mMediaProjectionManager1.getMediaProjection(mResultCode, mResultData);
        MyLog.i(TAG, "mMediaProjection defined");
    }

    private VirtualDisplay mVirtualDisplay = null;
    private int windowWidth = 0;
    private int windowHeight = 0;
    private ImageReader mImageReader = null;
    private int mScreenDensity = 0;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void virtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                windowWidth, windowHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
        MyLog.i(TAG, "virtual displayed");
    }

    private String pathImage = null;
    private SimpleDateFormat dateFormat = null;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startCapture() {
        //nameImage = pathImage+strDate+".png";
        //String nameImage = pathImage + "Tempq.png";
        MyLog.i("startCapture()");
        Image image = mImageReader.acquireLatestImage();
        Bitmap bitmap = null;
        if (image != null) {
            /*initDate();
            return;*/

        int width = image.getWidth();
        int height = image.getHeight();
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);

        bitmap = ImageUtils.compressImage(bitmap);
        // SendImage(bitmap);
        image.close();
        MyLog.i("startCapture()...end");
        }
        //发送心跳包
        //SendHeartMessage(nameImage, bitmap);
        SendImage(bitmap);

    }

    private WindowManager mWindowManager1 = null;
    private DisplayMetrics metrics = null;

    private void createVirtualEnvironment() {
        pathImage = Environment.getExternalStorageDirectory().getPath() + "/Pictures/";
        //nameImage = pathImage+strDate+".png";
        //String nameImage = pathImage + "Temp.png";
        mMediaProjectionManager1 = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mWindowManager1 = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        windowWidth = mWindowManager1.getDefaultDisplay().getWidth();
        windowHeight = mWindowManager1.getDefaultDisplay().getHeight();
        metrics = new DisplayMetrics();
        mWindowManager1.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mImageReader = ImageReader.newInstance(windowWidth, windowHeight, 0x1, 2); //ImageFormat.RGB_565

        MyLog.i(TAG, "prepared the virtual environment");
    }

    private void sendKeyCode(final int keyCode) {
        new Thread() {
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(keyCode);
                } catch (Exception e) {
                    MyLog.e("Exception when sendPointerSync", e.getMessage());
                }
            }
        }.start();
    }

    @Override
    public boolean onLongClick(View v) {
        // TODO Auto-generated method stub
        /*switch (v.getId()) {
        case R.id.float_id:
			LayoutInflater layout = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = layout.inflate(R.layout.keyboard, null);	
			pW= new PopupWindow(view,480,75,true);
//			pW = new PopupWindow(view, view.getLayoutParams().WRAP_CONTENT, view.getLayoutParams().WRAP_CONTENT, true);
			//	以下这行加上去后就可以使用BACK键关闭POPWINDOW
//			pW.setBackgroundDrawable(new ColorDrawable(0xb0000000));
			pW.setBackgroundDrawable(new ColorDrawable(0x00000000));
			//pW.setWidth(300);
			//pW.setHeight(60);
			pW.setOutsideTouchable(true);
			pW.setAnimationStyle(R.style.FromRightAnimation);//从右进入
			pW.setOnDismissListener(new PopupWindow.OnDismissListener(){	
				@Override
			public void onDismiss() {
					// TODO Auto-generated method stub	
				}
			});
//			pW.setAnimationStyle(android.R.style.Animation_Toast);
//			pW.setAnimationStyle(R.style.PopupAnimation);
	        int[] location = new int[2];
	        mFloatLayout.getLocationOnScreen(location);			
			pW.showAtLocation(mFloatLayout, Gravity.RIGHT,location[0],0);
			pW.update();	
			
		default:
			break;
		}*/
        return true;

    }

}
