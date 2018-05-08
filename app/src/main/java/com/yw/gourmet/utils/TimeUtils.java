package com.yw.gourmet.utils;

/**
 * auth: lewis-v
 * time: 2018/5/4.
 */

public class TimeUtils {

    /**
     * 将语聊的时间长度转化为字符串形式,只显示分钟和秒
     * @param length
     * @return
     */
    public static String getVoiceChatLength(int length){
        int min = length / (60);
        length -= min * 60;
        int se = length;
        return fillZero(min)+":"+fillZero(se);
    }

    /**
     * 不足2位补0
     * @param data
     * @return
     */
    public static String fillZero(int data){
        return data<10?("0"+data): String.valueOf(data);
    }
}
