package com.huan.administrator.screensharingmobile.bean;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
	
	private int id;
	private User sendUser;
	private User reciveUser;
	private String content;
	private Date time = new Date();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public User getSendUser() {
		return sendUser;
	}
	public void setSendUser(User sendUser) {
		this.sendUser = sendUser;
	}
	public User getReciveUser() {
		return reciveUser;
	}
	public void setReciveUser(User reciveUser) {
		this.reciveUser = reciveUser;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	
}
