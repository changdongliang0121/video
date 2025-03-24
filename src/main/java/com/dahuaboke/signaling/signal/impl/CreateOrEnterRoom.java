package com.dahuaboke.signaling.signal.impl;

import com.alibaba.fastjson2.JSONObject;
import com.dahuaboke.signaling.model.Cache;
import com.dahuaboke.signaling.model.Room;
import com.dahuaboke.signaling.signal.Signal;
import com.dahuaboke.signaling.util.WriteUtil;
import com.dahuaboke.signaling.vo.BaseInVo;
import com.dahuaboke.signaling.vo.Result;

public class CreateOrEnterRoom implements Signal {

    @Override
    public void run(BaseInVo baseInVo, String text) {
        if (baseInVo.getRoomId() == null || baseInVo.getRoomId().length() == 0) {
            WriteUtil.write(baseInVo.getPerson().getChannel(),Result.fail(baseInVo.getType(), "房间号不能为空"));
        }
        Room room = Cache.createOrEnterRoom(baseInVo.getRoomId(), baseInVo.getPerson());
        JSONObject json = new JSONObject();
        json.put("ruuid", room.getRuuid());
        WriteUtil.write(baseInVo.getPerson().getChannel(), Result.success(baseInVo.getType(),json.toJSONString()));
        Cache.pushRoomMessage(baseInVo.getPerson(),true);
    }
}
