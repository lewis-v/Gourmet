package com.yw.gourmet;

import com.yw.gourmet.data.UserData;

import java.util.List;

/**
 * Created by LYW on 2017/11/13.
 */

public class Constant {
    //用户信息
    public static UserData userData;
    //微信分享ID
    public static String APP_ID = "wxe4f13818ce83408c";

    public class TypeFlag{
        public static final int DIARY = 0;//日记
        public static final int RAIDERS = 1;//攻略
        public static final int MENU = 2;//食谱
        public static final int SHARE = 3;//普通分享
    }
    //省份信息
    public static List<String> areaList;

    public static long serviceTime = 0;

}
