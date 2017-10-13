package com.huan.administrator.screensharingmobile;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huan.administrator.screensharingmobile.base.BaseService;
import com.huan.administrator.screensharingmobile.bean.Message;
import com.huan.administrator.screensharingmobile.bean.UdpData;
import com.huan.administrator.screensharingmobile.bean.User;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class UdpService extends BaseService {
	private static final String TAG = "CGQ";
	private static DatagramSocket client;
	private static Thread receive_Thread;
	private static Thread heartbeat_thread;
	private static Thread sendmessage_thread;
	private String IP_Server_Str = "192.168.1.120", Port_Server_Str = "5057";
	private boolean isRunning = true;
	private String recvStr, send_Str;
	public static List<Message> result = new ArrayList<Message>();
	private MsgBinder mbinder = new MsgBinder();

	/**
	 * onBind 是 Service 的虚方法，因此我们不得不实现它。 返回 null，表示客服端不能建立到此服务的连接。
	 */
	@Override
	public IBinder onBind(Intent intent) {
		super.onBind(intent);
		Log.v(TAG, "ServiceDemo onBind");
		return mbinder;
	}

	public class MsgBinder extends Binder {
		/**
		 * 获取当前Service的实例
		 * @return
		 */
		public UdpService getService(){
			return UdpService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		ReceiveMessage();
		Log.v(TAG, "ServiceDemo onCreate");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.i(TAG, "ServiceDemo onStart");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "ServiceDemo onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	public void Login(String username, String password){
		Message message = new Message();
		message.setContent("ni hao a");
		UdpData udpData = new UdpData();
		sendMessage(udpData);
	}
	public void Logoff(String username, String password){

	}

	public void ReceiveMessage(){
		try {
			if(client==null) {
				client = new DatagramSocket();
			}
			if(receive_Thread==null){
				receive_Thread = new Thread(new ReceiveMessage_Thread());
				receive_Thread.start();
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
	}

	public static void SendHeartMessage(){
		if (heartbeat_thread==null) {
			heartbeat_thread = new Thread(new Heartbeat_Thread());
			heartbeat_thread.start();
		}
	}
	public static void sendMessage(final UdpData udata){
			sendmessage_thread = new Thread(new SendMessage_Thread(udata));
			sendmessage_thread.start();
	}

	// 心跳线程类
	static class Heartbeat_Thread implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(6000);
					// 开门
					Log.i("CGQ", "service:send message...");

					UdpData udpData = new UdpData();
					udpData.setId(001);
					Message message = new Message();
					message.setContent("ni hao a");
					User user_sender = new User();
					user_sender.setUsername("978252909");
					user_sender.setPassword("112233");
					user_sender.setId(1);
					User user_reciver = new User();
					user_reciver.setId(1);
					message.setReciveUser(user_reciver);
					message.setSendUser(user_sender);
					udpData.setMessage(message);

					String sendStr = JSON.toJSONString(udpData);
					Log.d(TAG, "sendStr=" + sendStr);
					byte[] sendBuf;
					sendBuf = sendStr.getBytes();
					InetAddress addr = InetAddress
							.getByName("192.168.31.144");
					int port = 5051;
					DatagramPacket sendPacket = new DatagramPacket(sendBuf,
							sendBuf.length, addr, port);
					client.send(sendPacket);

				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Log.i(TAG, "ServiceDemo is running...");
			}
		}
	}

	// 发送数据线程类
	static class SendMessage_Thread implements Runnable {
		UdpData udata;
		SendMessage_Thread(UdpData udata){
			this.udata = udata;
		}
		@Override
		public void run() {
			try{
				UdpData udpData = udata;
				String sendStr = JSON.toJSONString(udpData);
				Log.d(TAG, "sendStr="+sendStr);
				byte[] sendBuf;
				sendBuf = sendStr.getBytes();
				InetAddress addr = InetAddress.getByName("192.168.1.120");
				int port = 5057;
				DatagramPacket sendPacket = new DatagramPacket(sendBuf,
						sendBuf.length, addr, port);
				client.send(sendPacket);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 接收数据线程类
	static class ReceiveMessage_Thread implements Runnable {
		@Override
		public void run() {
			try {
				byte[] recvBuf = new byte[1000];
				DatagramPacket recvPacket = new DatagramPacket(recvBuf,
						recvBuf.length);
				while(true){
					client.receive(recvPacket);
					String recvStr = new String(recvPacket.getData(), 0,
							recvPacket.getLength());
					Log.d(TAG,"收到:" + recvStr);
					UdpData udpData = JSONObject.parseObject(recvStr,
							UdpData.class);
					//analysisData(udpData);
				}
			} catch (SocketException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/*private static void analysisData(UdpData udpData){
		Message message = udpData.getMessage();
		result.add(message);
		//manager.HaveNewMessage(message);
		Log.d(TAG,"message："+message.getContent());
		if(udpData.getMessage().getContent().equals("login success")){
			//登陆成功
			message.setContent("login success");
			MyselfApplication.getManager().LoginMessage(message);
			Log.d(TAG,"登陆成功！！！");
		}else {
			MyselfApplication.getManager().HaveNewMessage(message);
		}
	}*/
}
