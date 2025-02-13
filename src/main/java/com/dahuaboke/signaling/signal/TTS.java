package com.dahuaboke.signaling.signal;

import com.alibaba.fastjson2.JSONObject;
import com.dahuaboke.signaling.model.Cache;
import com.dahuaboke.signaling.model.Person;
import com.dahuaboke.signaling.util.FFmpegUtil;
import com.dahuaboke.signaling.util.WriteUtil;
import com.dahuaboke.signaling.vo.BaseInVo;
import com.dahuaboke.signaling.vo.Result;

public class TTS implements Signal {

    @Override
    public void run(BaseInVo baseInVo, String text) {
        Person person = baseInVo.getPerson();
        if(person.getProcess() != null){
            try{
                FFmpegUtil.terminateProcess(person.getProcess());
            }catch (Exception e){}
        }
        String key = person.getUserId() + "_" + System.currentTimeMillis();
        String streamKey = person.getRoomId() + "/" +  key;
        person.setTtsStreamKey(key);
        new Thread(() -> {
            FFmpegUtil.joinStream(person,streamKey);
        }).start();
//        WriteUtil.write(person.getChannel(), Result.success(baseInVo.getType(),key));
        String message = JSONObject.toJSONString(Result.success("tts", key));
        Cache.pushMessage(person,message,true);
    }

}
