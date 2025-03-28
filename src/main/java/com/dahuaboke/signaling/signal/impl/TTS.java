package com.dahuaboke.signaling.signal.impl;

import com.alibaba.fastjson2.JSONObject;
import com.dahuaboke.signaling.constants.Constant;
import com.dahuaboke.signaling.model.Cache;
import com.dahuaboke.signaling.model.Person;
import com.dahuaboke.signaling.model.Room;
import com.dahuaboke.signaling.signal.Signal;
import com.dahuaboke.signaling.util.FFmpegUtil;
import com.dahuaboke.signaling.util.NettyHttpClientUtil;
import com.dahuaboke.signaling.vo.BaseInVo;
import com.dahuaboke.signaling.vo.Result;

import javax.net.ssl.SSLException;
import java.net.URISyntaxException;

public class TTS implements Signal {



//    @Override
//    public synchronized void run(BaseInVo baseInVo, String text) {
//        Person person = baseInVo.getPerson();
//        Room room = Cache.rooms.get(baseInVo.getPerson().getRoomId());
//        if (room.getProcess() != null) {
//            try {
//                FFmpegUtil.terminateProcess(room.getProcess());
//            } catch (Exception e) {
//            }
//        }
//        JSONObject json = JSONObject.parseObject(text);
//        Integer num = json.getInteger("num");
//        if (num == null) {
//            num = new Integer(1);
//        }
//        Integer finalNum = num;
//        String streamKey = room.getRoomId() + "/" + room.getRuuid();
//        new Thread(() -> {
//            FFmpegUtil.joinStream(room, streamKey, Constant.fileArr[finalNum - 1]);
//        }).start();
//        String message = JSONObject.toJSONString(Result.success("tts", "success"));
//        Cache.pushMessage(person, message, true);
//    }

    @Override
    public synchronized void run(BaseInVo baseInVo, String text) {
        Person person = baseInVo.getPerson();
        String question = baseInVo.getQuestion();
        JSONObject jsonObject = new JSONObject();
        String msg = "";
        Room room = Cache.rooms.get(baseInVo.getPerson().getRoomId());

        String streamKey = room.getRoomId() + "/" + room.getRuuid();
        jsonObject.put("text",question);
        jsonObject.put("upload_url",streamKey);
        if (room.getProcess() != null) {
            try {
                FFmpegUtil.terminateProcess(room.getProcess());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JSONObject json = JSONObject.parseObject(text);


            try {
                 msg =  NettyHttpClientUtil.sendPostRequest(Constant.TTS_GEN_PATH,JSONObject.toJSONString(jsonObject));
            } catch (InterruptedException | URISyntaxException | SSLException e) {
                throw new RuntimeException(e);
            }
        String message = JSONObject.toJSONString(Result.success("tts", msg));
        Cache.pushMessage(person, message, true);
    }

    public static void main(String[] args) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("text","丁伟强大傻逼大傻逼啊大傻逼");
            jsonObject.put("upload_url","zsc/abc01 ");
            String a = NettyHttpClientUtil.sendPostRequest("http://dahuaboke.com:8082/tts",jsonObject.toString());
            System.out.println(a);
        } catch (InterruptedException | SSLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
