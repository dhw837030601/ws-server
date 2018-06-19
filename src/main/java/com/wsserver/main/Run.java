package com.wsserver.main;

import java.io.IOException;

import com.wsserver.netty.server.WebSocketServer;
import com.wsserver.utils.SpringUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Run {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		String[] xmls = new String[]{
				"spring.xml",
				"spring-mybatis.xml"
		};
		ApplicationContext act = new ClassPathXmlApplicationContext(xmls);
		SpringUtil.set(act);
		WebSocketServer wss = (WebSocketServer) SpringUtil.getBean("WebSocketServer");
		wss.running();
		
	}



}
