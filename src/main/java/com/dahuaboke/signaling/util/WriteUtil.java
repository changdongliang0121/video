package com.dahuaboke.signaling.util;

import com.alibaba.fastjson2.JSON;
import com.dahuaboke.signaling.vo.Result;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriteUtil {

    private static final Logger logger = LoggerFactory.getLogger(WriteUtil.class);

    public static void write(Channel channel, Result result) {
        logger.info("推送消息 ： " + JSON.toJSONString(result));
        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(result)));
    }

    public static void write(Channel channel, String message) {
        logger.info("推送消息 ： " + message);
        channel.writeAndFlush(new TextWebSocketFrame(message));
    }

}
