package com.wsserver.netty.model;

public class Msg {
	
	private String msg;
	
	private String fromId;
	
	public Msg(){
		
	}

	public Msg(String msg,String fromId){
		this.msg = msg;
		this.fromId = fromId;
	}
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getFromId() {
		return fromId;
	}

	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

	
}
