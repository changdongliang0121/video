package com.dahuaboke.signaling.signal;

import com.alibaba.fastjson2.JSONObject;
import com.dahuaboke.signaling.model.Cache;
import com.dahuaboke.signaling.util.WriteUtil;
import com.dahuaboke.signaling.vo.AddOrUpdateStreamKeyInvo;
import com.dahuaboke.signaling.vo.BaseInVo;
import com.dahuaboke.signaling.vo.Result;

public class AddOrUpdateStream implements Signal {

    @Override
    public void run(BaseInVo baseInVo, String text) {
        AddOrUpdateStreamKeyInvo addOrUpdateStreamKeyInvo = JSONObject.parseObject(text, AddOrUpdateStreamKeyInvo.class);
        baseInVo.getPerson().getStreamKey().put(addOrUpdateStreamKeyInvo.getStreamType(), addOrUpdateStreamKeyInvo.getStreamKey());
        WriteUtil.write(baseInVo.getPerson().getChannel(), Result.success(baseInVo.getType(),"success"));
        Cache.pushRoomMessage(baseInVo.getPerson(),true);
    }

}
