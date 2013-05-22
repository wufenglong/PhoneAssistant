package com.hwttnet.mobileassistant.util;

public class SMSUnit {
	private String _id;// id
	// private String thread_id;// thread_id
	private String address;// 电话号
	private String date;// 时间
	// private String read;
	// private String status;

	/**
	 * sms类型：
	 * 
	 * ALL =0;
	 * 
	 * INBOX =1; 收件箱
	 * 
	 * SENT=2; 已发送
	 * 
	 * DRAFT=3;草稿箱
	 * 
	 * OUTBOX =4;发件箱
	 * 
	 * FAILED =5;失败
	 * 
	 * QUEUED=6;发送排队
	 * 
	 * */
	private String type;// 类型
	private String body;// 短信内容

	// public String getThread_id() {
	// return thread_id;
	// }

	public String getAddress() {
		return address;
	}

	public String getDate() {
		return date;
	}

	// public String getRead() {
	// return read;
	// }
	//
	// public String getStatus() {
	// return status;
	// }

	public String getType() {
		return type;
	}

	public String getBody() {
		return body;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String id) {
		_id = id;
	}

	// public void setThread_id(String threadId) {
	// thread_id = threadId;
	// }

	public void setAddress(String address) {
		this.address = address;
	}

	public void setDate(String date) {
		this.date = date;
	}

	// public void setRead(String read) {
	// this.read = read;
	// }
	//
	// public void setStatus(String status) {
	// this.status = status;
	// }

	public void setType(String type) {
		this.type = type;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
