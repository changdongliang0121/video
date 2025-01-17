package com.dahuaboke.signaling.signal;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dahuaboke.signaling.model.Cache;
import com.dahuaboke.signaling.util.FFmpegUtil;
import com.dahuaboke.signaling.vo.BaseInVo;
import com.dahuaboke.signaling.vo.Result;

public class LeaveRoom implements Signal {

    @Override
    public void run(BaseInVo baseInVo, String text) {
        Cache.levelRoom(baseInVo.getPerson());
        Cache.removePerson(baseInVo.getPerson());
        if(baseInVo.getPerson().getProcess() != null){
            try{
                FFmpegUtil.terminateProcess(baseInVo.getPerson().getProcess());
            }catch (Exception e){}
        }
        JSONObject json = new JSONObject();
        json.put("userId", baseInVo.getPerson().getUserId());
        json.put("roomId", baseInVo.getPerson().getRoomId());
        Result result = Result.success(baseInVo.getType(), json.toString());
        Cache.pushMessage(baseInVo.getPerson(), JSON.toJSONString(result));
    }

}
