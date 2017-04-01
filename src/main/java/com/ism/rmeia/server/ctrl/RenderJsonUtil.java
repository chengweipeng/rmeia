package com.ism.rmeia.server.ctrl;

import com.alibaba.fastjson.JSON;

public class RenderJsonUtil {

    public static String getStatusJson(int status, String msg) {
        return JSON.toJSONString(new ServiceStatus(status, msg));
    }
}
