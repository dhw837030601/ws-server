package com.wsserver.netty.cache;

import io.netty.channel.ChannelHandlerContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserCache {

	private static Map<String,String> uCache;
	private static Map<String,ChannelHandlerContext> ctxCache;
	
	static{
		uCache = new HashMap<String,String>();
		ctxCache = new HashMap<String,ChannelHandlerContext>();
	}
	
	public static void set(String ctxId,ChannelHandlerContext ctx){
		ctxCache.put(ctxId, ctx);
	}

	public static void setRela(String uid,String ctxId){
		uCache.put(uid, ctxId);
	}

	public static ChannelHandlerContext get(String ctxId){
		return ctxCache.get(ctxId);
	}

	public static String getRela(String uid){
		return uCache.get(uid);
	}

	public static void removeRela(String ctxId){
		Set<String> keySet = uCache.keySet();
		if (keySet != null && keySet.size() > 0 ){
			for (String key:keySet) {
				String value = uCache.get(key);
				if (value.equals(ctxId)) {
					uCache.remove(key);
					break;
				}
			}
		}
	}
	
	public static void remove(String ctxId){
		ctxCache.remove(ctxId);
		removeRela(ctxId);
	}

	
}
