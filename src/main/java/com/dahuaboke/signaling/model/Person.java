package com.dahuaboke.signaling.model;

import com.alibaba.fastjson2.annotation.JSONField;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Person {

    private String uuid;

    private String userId;

    @JSONField(serialize = false)
    private String roomId;

    @JSONField(serialize = false)
    private Channel channel;

    private String ttsStreamKey;

    private Map<String, String> streamKey = new HashMap();

    public Person(Channel channel, String userId) {
        this.uuid = UUID.randomUUID().toString().toString().replaceAll("-", "");
        this.channel = channel;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Map<String, String> getStreamKey() {
        return streamKey;
    }

    public void setStreamKey(Map<String, String> streamKey) {
        this.streamKey = streamKey;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getTtsStreamKey() {
        return ttsStreamKey;
    }

    public void setTtsStreamKey(String ttsStreamKey) {
        this.ttsStreamKey = ttsStreamKey;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
