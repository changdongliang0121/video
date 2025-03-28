package com.dahuaboke.signaling.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dahuaboke.signaling.constants.Constant;
import com.dahuaboke.signaling.vo.Result;

import java.util.Arrays;

public class RoomController {

    private static final RoomController instance = new RoomController();

    public static String forward(String uri,String content) {
        Result result = null;
        switch (uri){
            case "/room/findQuestion":
                result = instance.findQuestion(JSONObject.parse(content));
                break;
            case "/room/findQuestionList":
                result = instance.findquestionList();
                break;
            default:
                result = Result.fail(uri,"404");
        }
        if (result != null)
            result.setType(uri);
        return JSONObject.toJSONString(result);
    }


    /**
     * 查询某个问题
     * @return
     */
    public Result findQuestion(JSONObject invo){
        Integer num = invo.getInteger("num");
        if (num == null){
            return Result.fail("","num字段必送");
        }
        if(num > 0 && num <= Constant.questionArr.length){
            return Result.success("", JSON.toJSONString(Constant.questionArr));
        }else{
            return Result.fail("","没有这个问题");
        }
    }
    /**
     * 查询某个问题
     * @return
     */
    public Result findquestionList(){
            return Result.success("", JSON.toJSONString(Constant.questionArr));
        }
}
