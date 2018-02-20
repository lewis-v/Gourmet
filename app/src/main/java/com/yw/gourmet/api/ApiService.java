package com.yw.gourmet.api;


import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.CommentData;
import com.yw.gourmet.data.FlowData;
import com.yw.gourmet.data.InitData;
import com.yw.gourmet.data.MenuDetailData;
import com.yw.gourmet.data.MenuPracticeData;
import com.yw.gourmet.data.MessageListData;
import com.yw.gourmet.data.RaidersDetailData;
import com.yw.gourmet.data.RaidersListData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.data.UserData;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by yw on 2017-08-08.
 *  @Multipart,@POST,@PartMap,@Part
 */

public interface ApiService {
    //初始化
    @GET("/Init")
    Observable<BaseData<InitData>> Init();

    //获取省级信息
    @GET("/Area/Get")
    Observable<BaseData<List<String>>> GetAreaDetail();

    //登录
    @Multipart
    @POST("/Login")
    Observable<BaseData<UserData>> Login(@Part List<MultipartBody.Part> parts);

    //获取用户信息
    @Multipart
    @POST("/User/Info/Get")
    Observable<BaseData<UserData>> GetUserInfo(@Part List<MultipartBody.Part> parts);

    //加载分享列表
    @Multipart
    @POST("/ShareList/Load")
    Observable<BaseData<List<ShareListData<List<String>>>>> LoadShareList(@Part List<MultipartBody.Part> parts);

    //加载消息列表
    @Multipart
    @POST("/Message/Get/List")
    Observable<BaseData<List<MessageListData>>> LoadMessageList(@Part List<MultipartBody.Part> parts);

    //点赞与踩
    @Multipart
    @POST("/ReMark/Put")
    Observable<BaseData<ShareListData<List<String>>>> PutReMark(@Part List<MultipartBody.Part> parts);

    //修改个人信息
    @Multipart
    @POST("/User/ChangeDetail")
    Observable<BaseData<UserData>> ChangeUserDetail(@Part List<MultipartBody.Part> parts);

    //上传照片
    @Multipart
    @POST("/Img/Up")
    Observable<BaseData<String>> UpImg(@Part List<MultipartBody.Part> parts);

    //普通分享
    @Multipart
    @POST("/Share/Common/Put")
    Observable<BaseData> ShareCommon(@Part List<MultipartBody.Part> parts);

    //日记分享
    @Multipart
    @POST("Share/Diary/Put")
    Observable<BaseData> ShareDiary(@Part List<MultipartBody.Part> parts);

    //食谱分享
    @Multipart
    @POST("/Share/Menu/Put")
    Observable<BaseData> ShareMenu(@Part List<MultipartBody.Part> parts);

    //攻略分享
    @Multipart
    @POST("/Share/Raiders/Put")
    Observable<BaseData<RaidersDetailData<List<RaidersListData<List<String>>>,List<String>>>> ShareRaiders(@Part List<MultipartBody.Part> parts);

    //获取聊天信息的详情
    @Multipart
    @POST("/Message/Get/Detail")
    Observable<BaseData<List<MessageListData>>> GetMessageDetail(@Part List<MultipartBody.Part> parts);

    //发送聊天信息
    @Multipart
    @POST("/Message/Put")
    Observable<BaseData> SendMessage(@Part List<MultipartBody.Part> parts);

    //设置消息已读
    @Multipart
    @POST("/Message/Read")
    Observable<BaseData> SetMessageRead(@Part List<MultipartBody.Part> parts);

    //获取评论信息
    @Multipart
    @POST("/Comment/Get")
    Observable<BaseData<List<CommentData>>> GetComment(@Part List<MultipartBody.Part> parts);

    //发送评论信息
    @Multipart
    @POST("/Comment/Put")
    Observable<BaseData<List<CommentData>>> PutComment(@Part List<MultipartBody.Part> parts);

    //获取个人评论信息
    @Multipart
    @POST("/Comment/My/Get")
    Observable<BaseData<List<ShareListData<List<String>>>>> GetCommentMy(@Part List<MultipartBody.Part> parts);

    //获取普通分享详情
    @Multipart
    @POST("/Share/Common/Get")
    Observable<BaseData<ShareListData<List<String>>>> GetCommonDetail(@Part List<MultipartBody.Part> parts);

    //获取日记详情
    @Multipart
    @POST("/Share/Diary/Get")
    Observable<BaseData<ShareListData>> GetShareDetail(@Part List<MultipartBody.Part> parts);

    //获取食谱详情
    @Multipart
    @POST("/Share/Menu/Get")
    Observable<BaseData<MenuDetailData<List<MenuPracticeData<List<String>>>,List<String>>>> GetMenuDetail(@Part List<MultipartBody.Part> parts);

    //获取攻略详情
    @Multipart
    @POST("/Share/Raiders/Get")
    Observable<BaseData<RaidersDetailData<List<RaidersListData<List<String>>>,List<String>>>> GetRaidersDetail(@Part List<MultipartBody.Part> parts);

    //收藏操作
    @Multipart
    @POST("/Collection/Put")
    Observable<BaseData> Collection(@Part List<MultipartBody.Part> parts);

    //获取收藏列表
    @Multipart
    @POST("/Collection/Get")
    Observable<BaseData<List<ShareListData<List<String>>>>> GetCollection(@Part List<MultipartBody.Part> parts);

    //获取置顶列表
    @Multipart
    @POST("/Top/Get")
    Observable<BaseData<List<ShareListData<List<String>>>>> GetTop(@Part List<MultipartBody.Part> parts);

    //设置置顶
    @Multipart
    @POST("/Top/Put")
    Observable<BaseData> PutTop(@Part List<MultipartBody.Part> parts);

    //获取滚动广告
    @GET("/Flow/Get")
    Observable<BaseData<List<FlowData>>> GetFlow();

    //搜索
    @Multipart
    @POST("/Find/Share")
    Observable<BaseData<List<ShareListData<List<String>>>>> Search(@Part List<MultipartBody.Part> parts);

    //检测账号和昵称是否已经被使用
    @Multipart
    @POST("/User/Check")
    Observable<BaseData> Check(@Part List<MultipartBody.Part> parts);

    //注册账号
    @Multipart
    @POST("/User/Registered")
    Observable<BaseData> Registered(@Part List<MultipartBody.Part> parts);

    //投诉
    @Multipart
    @POST("/User/Complaint")
    Observable<BaseData> Complaint(@Part List<MultipartBody.Part> parts);

    //反馈
    @Multipart
    @POST("/User/Feedback")
    Observable<BaseData> Feedback(@Part List<MultipartBody.Part> parts);
}
