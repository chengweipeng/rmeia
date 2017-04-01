package com.ism.rmeia.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ism.util.Config;

public class Enviroment {
    public static boolean devMode = false;

    /**
     * 设置获取SIG的时间，如果设为true，那么使用当天10点的数据
     */
    public static boolean sigDebugSettime = false;

    public static long sigDebugGetTime=0L;
    /**
     * SIG &E的获取频率，SIG&E的数据频率15分钟，因此必须小于15/2 单位秒
     */
    public static int sigReadPeriod = 600;
    /**
     * 向ia发送数据的HTTP url
     */
    public static String ia_send_url;

    public static void config(Config cfg) {
        devMode = cfg.getBoolean("dev.mode", false);
        sigReadPeriod = cfg.getInt("sig.read.period",600);
        ia_send_url = cfg.getString("ia.send.url", "http://192.168.20.143/adjust?");
        sigDebugSettime = cfg.getBoolean("sig.debug.settime", false);
        String strSigDebugGetTime = cfg.getString("sig.debug.get.time", "open");
        if ("open".equalsIgnoreCase(strSigDebugGetTime)) {
            //sigDebugGetTime = new Date().getTime();
        } else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            try {
                Date dt = new Date();
                dt = df.parse(strSigDebugGetTime);
                sigDebugGetTime = dt.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
                sigDebugGetTime=0L;
            }
        }
    }
}
