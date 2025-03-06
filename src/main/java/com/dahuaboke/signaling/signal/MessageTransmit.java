package com.dahuaboke.signaling.signal;

import com.dahuaboke.signaling.model.Cache;
import com.dahuaboke.signaling.vo.BaseInVo;

/**
 * author: dahua
 * date: 2025/2/13 9:53
 */
public class MessageTransmit implements Signal{

    @Override
    public void run(BaseInVo baseInVo, String text) {
        Cache.pushMessage(baseInVo.getPerson(),text,false);
    }

}
