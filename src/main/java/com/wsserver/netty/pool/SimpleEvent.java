package com.wsserver.netty.pool;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wsserver.netty.model.Packet;

public interface SimpleEvent {

	static final Logger log = LoggerFactory.getLogger(SimpleEvent.class);

	public void onEvent(Packet pkg,ChannelHandlerContext ctx);

}
