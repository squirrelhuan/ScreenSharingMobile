package com.huan.administrator.screensharingmobile.bean;

import java.io.Serializable;

public class UdpData implements Serializable {

	private int id;
	private Message message;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Message getMessage() {
		return message;
	}
	public void setMessage(Message message) {
		this.message = message;
	}
	
}
