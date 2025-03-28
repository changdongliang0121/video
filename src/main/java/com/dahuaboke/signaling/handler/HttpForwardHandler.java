package com.dahuaboke.signaling.handler;

import com.alibaba.fastjson2.JSONObject;
import com.dahuaboke.signaling.controller.RoomController;
import com.dahuaboke.signaling.vo.Result;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.nio.charset.Charset;

public class HttpForwardHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        // 判断是否为 WebSocket 握手请求
        if (isWebSocketUpgradeRequest(msg)) {
            // 如果是 WebSocket 握手请求，直接传递给下一个处理器
            ctx.fireChannelRead(msg.retain());
            return;
        }

        String body = msg.content().toString(Charset.defaultCharset());
        System.out.println("[" + msg.uri() + "]" + body);

        String result = "";

        if(msg.method() != HttpMethod.POST && !body.isEmpty()){
            result = JSONObject.toJSONString(Result.fail(msg.uri(),"必须是post请求"));
        }

        if (result.equals("") && msg.uri().startsWith("/room")){
            result = RoomController.forward(msg.uri(),body);
        }

//        if (result.equals("") && msg.uri().startsWith("/qustionlist")){
//            result = RoomController.forward(msg.uri(),body);
//        }



        // 构建响应
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK
        );
        response.content().writeBytes(result.getBytes());
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        // 发送响应
        ctx.writeAndFlush(response);
    }

    /**
     * 判断是否是websocket请求
     *
     * @param req
     * @return
     */
    private boolean isWebSocketUpgradeRequest(FullHttpRequest req) {
        return req.headers().contains(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET, true)
                && req.headers().contains(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
