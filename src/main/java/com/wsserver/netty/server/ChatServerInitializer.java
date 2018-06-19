package com.wsserver.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatServerInitializer extends ChannelInitializer<Channel> {


    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("http-codec", new HttpServerCodec()); // Http消息编码解码  
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536)); // Http消息组装  
        pipeline.addLast("http-chunked", new ChunkedWriteHandler()); // WebSocket通信支持  
        pipeline.addLast("idleStateHandler", new IdleStateHandler(60,0,0));  
        pipeline.addLast("handler",new PackageHandler());
    }
 
}