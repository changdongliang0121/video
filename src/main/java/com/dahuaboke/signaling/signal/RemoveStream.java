package com.dahuaboke.signaling.signal;

import com.alibaba.fastjson2.JSONObject;
import com.dahuaboke.signaling.model.Cache;
import com.dahuaboke.signaling.util.WriteUtil;
import com.dahuaboke.signaling.vo.BaseInVo;
import com.dahuaboke.signaling.vo.RemoveStreamKeyInvo;
import com.dahuaboke.signaling.vo.Result;

public class RemoveStream implements Signal {

    @Override
    public void run(BaseInVo baseInVo, String text) {
        RemoveStreamKeyInvo removeStreamKeyInvo = JSONObject.parseObject(text, RemoveStreamKeyInvo.class);
        baseInVo.getPerson().getStreamKey().remove(removeStreamKeyInvo.getStreamType());
        WriteUtil.write(baseInVo.getPerson().getChannel(),Result.success(baseInVo.getType(), "success"));
        Cache.pushRoomMessage(baseInVo.getPerson(),false);
    }

}
