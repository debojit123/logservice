package com.loggerservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LoggerModel {

	private String id;
	private String state;
	private String host;
	private String type;
	private Long timestamp;

	@JsonIgnore
	private String alert;

	@JsonIgnore
	private Long consumedTime;

	public Long getConsumedTime() {
		return consumedTime;
	}

	public void setConsumedTime(Long consumedTime) {
		this.consumedTime = consumedTime;
	}

	public String getAlert() {
		return alert;
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

}
