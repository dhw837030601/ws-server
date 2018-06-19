package com.wsserver.netty.pool;

import java.util.HashMap;
import java.util.Map;

import com.wsserver.utils.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wsserver.netty.model.Packet;
import org.springframework.beans.factory.annotation.Autowired;



public class EventPool {

	@Autowired
	private EventPoolBuilder epb;

	private static Logger log = LoggerFactory.getLogger(EventPool.class);
	
	private Map<String,SimpleEvent> pool;

	public EventPool(){
		pool = new HashMap<String, SimpleEvent>();
		EventPoolBuilder epb = (EventPoolBuilder) SpringUtil.getBean("epb");
		epb.build(this);
	}
	
	public void addEvent(String code,SimpleEvent event){
		pool.put(code, event);
	}
	
	public void onEvent(Packet p,ChannelHandlerContext ctx){
		log.info("on event code "+p.getCode()+" from "+p.getFromId());
		SimpleEvent se = pool.get(p.getCode());
		if(se!=null){
			se.onEvent(p,ctx);
		}
	}

}
