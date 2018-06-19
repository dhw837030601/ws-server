package com.wsserver.netty.cache;

import com.wsserver.netty.model.Packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class User {
	
	private String id;
	
	private String name;
	
	private ChannelHandlerContext ctx;
	
	public void sendMsg(Packet msg) {
		ctx.channel().writeAndFlush(new TextWebSocketFrame(msg.toJson()));
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

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
	
	

}
