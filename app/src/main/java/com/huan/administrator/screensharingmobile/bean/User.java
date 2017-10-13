package com.huan.administrator.screensharingmobile.bean;

import android.media.Image;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
	private int id;
	private String username;
	private String password;
	private String nickname;
	private Image headimage;
	private Date date = new Date();
	private String ip;
	private int port;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	public Image getHeadimage() {
		return headimage;
	}

	public void setHeadimage(Image headimage) {
		this.headimage = headimage;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}
