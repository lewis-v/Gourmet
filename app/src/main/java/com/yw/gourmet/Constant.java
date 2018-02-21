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

    public class CommentType{
        public static final int ALL = 0;//全部
        public static final int COMMENT = 1;//评论
        public static final int GOOD = 2;//赞
        public static final int BAD = 3;//踩
    }
    //省份信息
    public static List<String> areaList;

    public static long serviceTime = 0;

    public final static String COUNTRY_CODE ="86";//国家手机号码,目前仅支持86中国

    //推送默认id
    public static final int NORMAL_PUSH_ID = 49573;
    //更新id
    public static final int UPDATE_ID = 34974;
}
