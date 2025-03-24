package com.dahuaboke.signaling.signal;

import com.dahuaboke.signaling.signal.impl.CreateOrEnterRoom;
import com.dahuaboke.signaling.signal.impl.LeaveRoom;
import com.dahuaboke.signaling.signal.impl.MessageTransmit;
import com.dahuaboke.signaling.signal.impl.TTS;
import com.dahuaboke.signaling.util.WriteUtil;
import com.dahuaboke.signaling.vo.BaseInVo;
import com.dahuaboke.signaling.vo.Result;

import java.util.HashMap;
import java.util.Map;

public class DoSignal {

    private static Map<SignalType, Signal> signals = new HashMap<SignalType, Signal>();

    public enum SignalType {
        CREATE_ORENTER_ROOM("createOrEnterRoom"),
        LEAVE_ROOM("leaveRoom"),
        ADDORUPDATE_STREAM("addOrUpdateStream"),
        REMOVE_STREAM("removeStream"),
        TTS("tts"),
        TRANSMIT("transmit");

        private String value;

        SignalType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private static SignalType findSignalType(String signalName) {
        for (SignalType signalType : SignalType.values()) {
            if (signalType.value.equals(signalName)) {
                return signalType;
            }
        }
        return null;
    }

    static {
        signals.put(SignalType.CREATE_ORENTER_ROOM, new CreateOrEnterRoom());
        signals.put(SignalType.LEAVE_ROOM, new LeaveRoom());
        signals.put(SignalType.TTS, new TTS());
        signals.put(SignalType.TRANSMIT, new MessageTransmit());
    }

    public static void signal(BaseInVo baseInVo, String text) {
        try {
            Signal signal = signals.get(findSignalType(baseInVo.getType()));
            if (signal == null) {
                WriteUtil.write(baseInVo.getPerson().getChannel(), Result.fail(baseInVo.getType(), "type不存在"));
            } else {
                signal.run(baseInVo, text);
            }
        } catch (Exception e) {
            WriteUtil.write(baseInVo.getPerson().getChannel(), Result.fail(baseInVo.getType(), "执行错误 : " + e.getMessage()));
        }
    }

}
