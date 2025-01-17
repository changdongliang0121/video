package com.dahuaboke.signaling.signal;

import com.dahuaboke.signaling.model.Cache;
import com.dahuaboke.signaling.util.WriteUtil;
import com.dahuaboke.signaling.vo.BaseInVo;
import com.dahuaboke.signaling.vo.Result;

public class CreateOrEnterRoom implements Signal {

    @Override
    public void run(BaseInVo baseInVo, String text) {
        if (baseInVo.getRoomId() == null || baseInVo.getRoomId().length() == 0) {
            WriteUtil.write(baseInVo.getPerson().getChannel(),Result.fail(baseInVo.getType(), "房间号不能为空"));
        }
        Cache.createOrEnterRoom(baseInVo.getRoomId(), baseInVo.getPerson());
        WriteUtil.write(baseInVo.getPerson().getChannel(), Result.success(baseInVo.getType(),"success"));
        Cache.pushRoomMessage(baseInVo.getPerson(),true);
    }
}
