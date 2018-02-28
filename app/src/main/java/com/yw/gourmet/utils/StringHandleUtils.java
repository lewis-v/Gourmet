package com.yw.gourmet.utils;

/**
 * auth: lewis-v
 * time: 2018/2/28.
 * 删除字符串头部和尾部的回车
 */

public class StringHandleUtils {

    public static String deleteEnter(String string){
        return deleteEnd(deleteStart(string));

    }

    /**
     * 删除首部的换行
     * @param str
     * @return
     */
    public static String deleteStart(String str){
        if (str.startsWith("\r") || str.startsWith("\n")){
            return deleteStart(str.substring(1));
        }
        return str;
    }

    /**
     * 删除尾部的换行
     * @param str
     * @return
     */
    public static String deleteEnd(String str){
        if (str.endsWith("\r") || str.endsWith("\n")){
            return deleteEnd(str.substring(0,str.length()-1));
        }
        return str;
    }
}
