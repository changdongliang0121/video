package com.dahuaboke.signaling.signal;

import com.dahuaboke.signaling.util.WriteUtil;
import com.dahuaboke.signaling.vo.BaseInVo;
import com.dahuaboke.signaling.vo.Result;

import java.util.HashMap;
import java.util.Map;

public class DoSignal {

    private static Map<String, Signal> signals = new HashMap<String, Signal>();

    static{
        signals.put("createOrEnterRoom", new CreateOrEnterRoom());
        signals.put("leaveRoom", new LeaveRoom());
        signals.put("addOrUpdateStream", new AddOrUpdateStream());
        signals.put("removeStream", new RemoveStream());
        signals.put("tts", new TTS());
    }

    public static void signal(BaseInVo baseInVo, String text) {
        try {
            Signal signal = signals.get(baseInVo.getType());
            if (signal == null) {
                WriteUtil.write(baseInVo.getPerson().getChannel(), Result.fail(baseInVo.getType(), "type不存在"));
            }else{
                signal.run(baseInVo, text);
            }
        } catch (Exception e) {
            WriteUtil.write(baseInVo.getPerson().getChannel(), Result.fail(baseInVo.getType(), "执行错误 : " + e.getMessage()));
        }
    }

}
