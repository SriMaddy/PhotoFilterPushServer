package com.example.dbaccess.token;

public class NotificationBean {

	private long id;	
	private String title;
	private String message;
	private long timestamp;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public String toString() {
		return "{"
				+ "\"notification\" :"  + "{"
								+ "\"id\" :" + "\"" + id + "\","
								+ "\"title\" :" + "\"" + title + "\","
								+ "\"message\" :" + "\"" + message + "\","
								+ "\"timestamp\" :" + "\"" + timestamp + "\""
								+ "}"
				+ "}" ;
	}
}
