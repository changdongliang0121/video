package com.dahuaboke.signaling.signal;

import com.alibaba.fastjson2.JSONObject;
import com.dahuaboke.signaling.model.Cache;
import com.dahuaboke.signaling.vo.BaseInVo;
import com.dahuaboke.signaling.vo.Result;

/**
 * author: dahua
 * date: 2025/2/13 9:53
 */
public class MessageForward implements Signal{

    @Override
    public void run(BaseInVo baseInVo, String text) {
        JSONObject jsonObject = JSONObject.parseObject(text);
        Result result = Result.success("wb-draw",jsonObject.getString("message"));
        String jsonString = JSONObject.toJSONString(result);
        Cache.pushMessage(baseInVo.getPerson(),jsonString,false);
    }

}
