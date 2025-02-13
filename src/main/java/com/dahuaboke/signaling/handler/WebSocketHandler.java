package com.dahuaboke.signaling.handler;

import com.alibaba.fastjson2.JSON;
import com.dahuaboke.signaling.model.Cache;
import com.dahuaboke.signaling.model.Person;
import com.dahuaboke.signaling.signal.DoSignal;
import com.dahuaboke.signaling.util.FFmpegUtil;
import com.dahuaboke.signaling.util.WriteUtil;
import com.dahuaboke.signaling.vo.BaseInVo;
import com.dahuaboke.signaling.vo.Result;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dahua
 * @time 2021/9/16 16:59
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) {
        Channel channel = channelHandlerContext.channel();
        String text = textWebSocketFrame.text();
        logger.info("入参信息 : {}",text);
        BaseInVo baseInvo;
        try {
            baseInvo = JSON.parseObject(text, BaseInVo.class);
        } catch (Exception e) {
            WriteUtil.write(channel, Result.fail("error", "报文转换错误"));
            return;
        }
        if (baseInvo.getType() == null || baseInvo.getType().length() == 0) {
            WriteUtil.write(channel, Result.fail("error", "type不能为空"));
            return;
        }
        Person person = Cache.persons.get(channel);
        if (person == null) {
            if (baseInvo.getUserId() == null || "".equals(baseInvo.getUserId())) {
                WriteUtil.write(channel, Result.fail("error", "用户信息缺失"));
                return;
            } else {
                person = new Person(channel, baseInvo.getUserId());
                Cache.persons.put(channel,person);
            }
        }
        baseInvo.setPerson(person);
        DoSignal.signal(baseInvo, text);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        BaseInVo baseInvo = new BaseInVo();
        baseInvo.setType(DoSignal.SignalType.LEAVE_ROOM.getValue());
        Person person = Cache.persons.get(ctx.channel());
        if (person != null) {
            baseInvo.setPerson(Cache.persons.get(ctx.channel()));
            DoSignal.signal(baseInvo, null);
        }
        super.channelInactive(ctx);
    }

}
