package com.dahuaboke.signaling.signal.impl;

import com.alibaba.fastjson2.JSONObject;
import com.dahuaboke.signaling.constants.Constant;
import com.dahuaboke.signaling.model.Cache;
import com.dahuaboke.signaling.model.Person;
import com.dahuaboke.signaling.model.Room;
import com.dahuaboke.signaling.signal.Signal;
import com.dahuaboke.signaling.util.FFmpegUtil;
import com.dahuaboke.signaling.vo.BaseInVo;
import com.dahuaboke.signaling.vo.Result;

public class TTS implements Signal {



    @Override
    public synchronized void run(BaseInVo baseInVo, String text) {
        Person person = baseInVo.getPerson();
        Room room = Cache.rooms.get(baseInVo.getPerson().getRoomId());
        if (room.getProcess() != null) {
            try {
                FFmpegUtil.terminateProcess(room.getProcess());
            } catch (Exception e) {
            }
        }
        JSONObject json = JSONObject.parseObject(text);
        Integer num = json.getInteger("num");
        if (num == null) {
            num = new Integer(1);
        }
        Integer finalNum = num;
        String streamKey = room.getRoomId() + "/" + room.getRuuid();
        new Thread(() -> {
            FFmpegUtil.joinStream(room, streamKey, Constant.fileArr[finalNum - 1]);
        }).start();
        String message = JSONObject.toJSONString(Result.success("tts", "success"));
        Cache.pushMessage(person, message, true);
    }

}
