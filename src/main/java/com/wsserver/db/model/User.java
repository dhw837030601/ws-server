package com.wsserver.db.model;


import com.wsserver.utils.MD5Utils;

public class User {

	private static final String TOKENSTR = "ws_server_lrs_niubi";
	
	private String id;
	
	private String name;
	
	private String account;

	private String pwd;

	private Long regTime;

	private String token;

	public User(){

	}
	public User(String id,String account,String token){
		this.id = id;
		this.account = account;
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public Long getRegTime() {
		return regTime;
	}

	public void setRegTime(Long regTime) {
		this.regTime = regTime;
	}

	public void buildToken(){
		this.token =  MD5Utils.md5(this.id + this.account + TOKENSTR);
		this.pwd = null;
	}
	public boolean validateToken(){
		String tk = MD5Utils.md5(this.id + this.account + TOKENSTR);
		return tk.equals(this.token);
	}
}
