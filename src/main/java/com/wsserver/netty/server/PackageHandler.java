package com.wsserver.netty.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wsserver.netty.cache.UserCache;
import com.wsserver.netty.model.Packet;
import com.wsserver.netty.pool.EventPool;

public class PackageHandler extends
		SimpleChannelInboundHandler<Object> {

	private static Logger log = LoggerFactory.getLogger(PackageHandler.class);


	private static EventPool ep = new EventPool();

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		String id = ctx.channel().id().toString();
		UserCache.set(id,ctx);
		log.info("id "+id+" join!");
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		String id = ctx.channel().id().toString();
		UserCache.remove(id);
		log.info("id "+id+" leave room !");
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				log.warn("60s read idle close context");
				ctx.close();
			}
		}
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if (msg instanceof FullHttpRequest) { // 传统的HTTP接入
			handleHttpRequest(ctx, (FullHttpRequest) msg);
		} else if (msg instanceof WebSocketFrame) { // WebSocket接入
			handleWebSocketFrame(ctx, (WebSocketFrame) msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		log.error("connect error close ChannelHandlerContext.", cause);
		ctx.close();
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@SuppressWarnings("deprecation")
	private void handleHttpRequest(ChannelHandlerContext ctx,
			FullHttpRequest request) throws Exception {
		// 如果HTTP解码失败，返回HHTP异常
		if (!request.getDecoderResult().isSuccess()
				|| (!"websocket".equals(request.headers().get("Upgrade")))) {
			sendHttpResponse(ctx, request, new DefaultFullHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}

		// 正常WebSocket的Http连接请求，构造握手响应返回
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
				"ws://" + request.headers().get(HttpHeaders.Names.HOST), null,
				false);
		WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(request);
		if (handshaker == null) { // 无法处理的websocket版本
			WebSocketServerHandshakerFactory
					.sendUnsupportedWebSocketVersionResponse(ctx.channel());
		} else { // 向客户端发送websocket握手,完成握手
			handshaker.handshake(ctx.channel(), request);
//			String id = ctx.channel().id().toString();
//			User u = new User();
//			u.setId(id);
//			u.setCtx(ctx);
//			UserCache.set(id, u);
//			Packet p = new Packet("handshake");
//			p.setBody(u);
//			u.sendMsg(p);
			// 记录管道处理上下文，便于服务器推送数据到客户端
			// this.ctx = ctx;
		}
	}

	

	/**
	 * Http返回
	 * 
	 * @param ctx
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("deprecation")
	private static void sendHttpResponse(ChannelHandlerContext ctx,
			FullHttpRequest request, FullHttpResponse response) {
		// 返回应答给客户端
		if (response.getStatus().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(
					response.getStatus().toString(), CharsetUtil.UTF_8);
			response.content().writeBytes(buf);
			buf.release();
			HttpHeaders.setContentLength(response, response.content()
					.readableBytes());
		}

		// 如果是非Keep-Alive，关闭连接
		ChannelFuture f = ctx.channel().writeAndFlush(response);
		if (!HttpHeaders.isKeepAlive(request)
				|| response.getStatus().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	/**
	 * 处理Socket请求
	 * 
	 * @param ctx
	 * @param frame
	 * @throws Exception
	 */
	private void handleWebSocketFrame(ChannelHandlerContext ctx,
			WebSocketFrame frame) throws Exception {
		// 判断是否是关闭链路的指令
		if (frame instanceof CloseWebSocketFrame) {
			// handshaker.close(ctx.channel(), (CloseWebSocketFrame)
			// frame.retain());
			return;
		}
		// 判断是否是Ping消息
		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(
					new PongWebSocketFrame(frame.content().retain()));
			return;
		} 
		// 当前只支持文本消息，不支持二进制消息
		if (!(frame instanceof TextWebSocketFrame)) {
			throw new UnsupportedOperationException("当前只支持文本消息，不支持二进制消息");
		}

		// 处理来自客户端的WebSocket请求
		try { 
			String id = ctx.channel().id().toString();
			String str = ((TextWebSocketFrame)frame).text();
			JSONObject obj = JSON.parseObject(str);
			String code = obj.getString("code");
			Packet p = new Packet(code,id);
			JSONObject body = obj.getJSONObject("body");
			if(body!=null&&!body.isEmpty()){
				p.setBody(body.toJSONString());
			}
			ep.onEvent(p,ctx);
		} catch (Exception e) {
			log.error("处理Socket请求异常", e);
		}
	}
}
