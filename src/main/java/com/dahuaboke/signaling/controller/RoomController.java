package com.dahuaboke.signaling.controller;

import com.alibaba.fastjson2.JSONObject;
import com.dahuaboke.signaling.constants.Constant;
import com.dahuaboke.signaling.vo.Result;

public class RoomController {

    private static final RoomController instance = new RoomController();

    public static String forward(String uri,String content) {
        Result result = null;
        switch (uri){
            case "/room/findquestion":
                result = instance.findquestion(JSONObject.parse(content));
                break;
            default:
                result = Result.fail(uri,"404");
        }
        if (result != null)
            result.setType(uri);
        return JSONObject.toJSONString(result);
    }


    /**
     * 查询问题
     * @param invo
     * @return
     */
    public Result findquestion(JSONObject invo){
        Integer num = invo.getInteger("num");
        if (num == null){
            return Result.fail("","num字段必送");
        }
        if(num > 0 && num <= Constant.questionArr.length){
            return Result.success("",Constant.questionArr[num - 1 ]);
        }else{
            return Result.fail("","没有这个问题");
        }
    }

}
