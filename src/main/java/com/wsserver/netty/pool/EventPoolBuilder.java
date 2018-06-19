package com.wsserver.netty.pool;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wsserver.db.dao.UserMapper;
import com.wsserver.db.model.Friend;
import com.wsserver.db.model.User;
import com.wsserver.netty.cache.UserCache;
import com.wsserver.netty.model.Msg;
import com.wsserver.netty.model.Packet;
import com.wsserver.utils.MD5Utils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("epb")
public class EventPoolBuilder {

	@Autowired
	private UserMapper dao;

	private void sendMsg(Packet msg,ChannelHandlerContext ctx){
		ctx.channel().writeAndFlush(new TextWebSocketFrame(msg.toJson()));
	}

	private boolean validateToken(JSONObject obj){
		User u = new User(obj.getString("id"),obj.getString("account"),obj.getString("token"));
		return u.validateToken();
	}
	
	public void build(final EventPool ep){
//		ep.addEvent("msg", new SimpleEvent() {
//			@Override
//			public void onEvent(Packet pkg) {
//				JSONObject obj = JSON.parseObject(pkg.getBody().toString());
//				Msg m = new Msg(obj.getString("msg"),pkg.getFromId());
//				Packet p = new Packet("msg");
//				p.setBody(m);
//				Collection<User> coll = UserCache.getAll();
//				for(User u:coll){
//					u.sendMsg(p);
//				}
//			}
//		});
//		ep.addEvent("rename", new SimpleEvent() {
//			@Override
//			public void onEvent(Packet pkg) {
//				JSONObject obj = JSON.parseObject(pkg.getBody().toString());
//				String id = pkg.getFromId();
//				User u = UserCache.get(id);
//				u.setName(obj.getString("name"));
//				Packet p = new Packet("rename");
//				p.setBody(u.getName());
//				u.sendMsg(p);
//
//				ep.onEvent(new Packet("online","system"));
//			}
//		});
//		ep.addEvent("online", new SimpleEvent() {
//			@Override
//			public void onEvent(Packet pkg) {
//				Collection<User> coll = UserCache.getAll();
//				pkg.setBody(coll);
//				for(User u1:coll){
//					u1.sendMsg(pkg);
//				}
//			}
//		});
		ep.addEvent("login", new SimpleEvent() {
			@Override
			public void onEvent(Packet pkg,ChannelHandlerContext ctx) {
				JSONObject obj = JSON.parseObject(pkg.getBody().toString());
				String acc = obj.getString("account");
				String pwd = obj.getString("pwd");
				User u = dao.getUser(acc);
				if (u != null) {
					pwd = StringUtils.isNotEmpty(pwd)? MD5Utils.md5(pwd):null;
					if (pwd!=null&&pwd.equals(u.getPwd())){
						UserCache.setRela(u.getId(),pkg.getFromId());
						u.buildToken();
						Packet p = new Packet("login","system",u);
						sendMsg(p,ctx);
					}else{
						Packet p = new Packet("err","system","密码错误");
						sendMsg(p,ctx);
					}
				} else {
					Packet p = new Packet("err","system","用户不存在");
					sendMsg(p,ctx);
				}

			}
		});
		ep.addEvent("fList", new SimpleEvent() {
			@Override
			public void onEvent(Packet pkg,ChannelHandlerContext ctx) {
				System.out.println(pkg.getBody());
				JSONObject obj = JSON.parseObject(pkg.getBody().toString());
				if (!validateToken(obj)) {
					Packet p = new Packet("err","system","登陆无效,请重新登陆");
					sendMsg(p,ctx);
				} else {
					List<Friend> fList = dao.fList(obj.getString("id"));
					Packet p = new Packet("fList","system",fList);
					sendMsg(p,ctx);
				}
			}
		});
		ep.addEvent("msg", new SimpleEvent() {
			@Override
			public void onEvent(Packet pkg,ChannelHandlerContext ctx) {
				JSONObject obj = JSON.parseObject(pkg.getBody().toString());
				if (!validateToken(obj)) {
					Packet p = new Packet("err","system","登陆无效,请重新登陆");
					sendMsg(p,ctx);
				} else {
					String to = obj.getString("to");
					Msg m = new Msg(obj.getString("msg"),obj.getString("id"));
					Packet resPkg = new Packet("msg",pkg.getFromId(),m);
					String ctxId = UserCache.getRela(to);
					if (StringUtils.isEmpty(ctxId)) {
						Packet p = new Packet("err","system","目标用户不在线");
						sendMsg(p,ctx);
					} else {
						sendMsg(resPkg,UserCache.get(ctxId));
					}
				}
			}
		});
	}
	
}
