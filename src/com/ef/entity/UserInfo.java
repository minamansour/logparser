package com.ef.entity;

import java.io.Serializable;
import java.util.Date;

/*
 * @Auther Mina Mansour
 * @Date 1/10/2017
 */
public class UserInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String IP;

	private Date loginTime;

	private String request;

	private String comment;

	private Integer requestCount;

	public UserInfo() {

	}

	public UserInfo(String IP, String comment, Integer requestCount) {
		this.IP = IP;
		this.comment = comment;
		this.requestCount = requestCount;
	}

	public Integer getRequestCount() {
		return requestCount;
	}

	public void setRequestCount(Integer requestCount) {
		this.requestCount = requestCount;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String IP) {
		this.IP = IP;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}
}
