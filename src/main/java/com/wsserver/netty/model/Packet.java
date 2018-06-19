package com.wsserver.netty.model;

import com.alibaba.fastjson.JSON;

public class Packet {
	
	private String code;
	
	private String fromId;
	
	private Object body;
	
	public Packet(String code,String fromId){
		this.code = code;
		this.fromId = fromId;
	}

	public Packet(String code,String fromId,Object body){
		this.code = code;
		this.fromId = fromId;
		this.body = body;
	}
	
	public Packet(String code){
		this.code = code;
	}
	
	public String toJson(){
		return JSON.toJSONString(this);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public String getFromId() {
		return fromId;
	}

	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

}
