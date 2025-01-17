package com.dahuaboke.signaling.signal;

import com.dahuaboke.signaling.vo.BaseInVo;

public interface Signal {

    void run(BaseInVo baseInVo, String text);

}
