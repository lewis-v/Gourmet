package com.yw.gourmet.api;


import com.yw.gourmet.Constant;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.CommentData;
import com.yw.gourmet.data.FlowData;
import com.yw.gourmet.data.HotSearch;
import com.yw.gourmet.data.InitData;
import com.yw.gourmet.data.MenuDetailData;
import com.yw.gourmet.data.MenuPracticeData;
import com.yw.gourmet.data.MessageListData;
import com.yw.gourmet.data.RaidersDetailData;
import com.yw.gourmet.data.RaidersListData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.data.UserData;
import com.yw.gourmet.utils.MD5;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by yw 2017-08-08.
 */

public class Api {
    public static String API_BASE_URL = "http://39.108.236.30:47423";//这里是服务器连接的接口的固定部分 39.108.236.30/120.79.65.50
    public static Api instance;//单例
    private ApiService service;//声明apiservier,下面要通过这个调用与服务器交互的方法
    private OkHttpClient okHttpClient;

    //添加请求头拦截器,这个头部也可以在apiservice这个接口中添加
    Interceptor mInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            String time = String.valueOf(System.currentTimeMillis()+ Constant.serviceTime);//时间戳
            String head = MD5.getMd5("head:"+time);//头部
            //这里添加头部,这里可以用addHeader来添加多个头部,如果使用header方法就只能添加一个头部
            Request.Builder requestBuilder = original.newBuilder()//添加头部信息
                    .addHeader("head", head)
                    .addHeader("time",time);
            Request request = requestBuilder.build();//用设置好的requestBuilder建立一个新的request
            return chain.proceed(request);
        }
    };

    /**
     * 因服务器迁移临时加入的方法,此处不建议使用,后期会直接删除
     * @param url
     */
    public static void reSet(String url){
        API_BASE_URL = url;
        instance = new Api();
    }

    public Api() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();//打印出请求的信息的拦截器
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);//设置打印请求的记录级别  NONE:不记录; BASIC:请求/响应行; HEADER:请求/响应行+请求头; BODY:请求/响应行+请求头+请求体(所有信息)

        okHttpClient = new OkHttpClient.Builder()//建立OkHttpClient,OkHttp3的连接及设置
                .connectTimeout(40 * 1000, TimeUnit.MILLISECONDS)//请求连接超时40秒
                .readTimeout(60 * 1000, TimeUnit.MILLISECONDS)//数据传输超时60秒
                .addInterceptor(mInterceptor)//添加请求头的拦截器
                .addInterceptor(interceptor)//打印请求信息拦截器
                .retryOnConnectionFailure(true)//连接失败后重试(断网重连)
                .build();


        Retrofit retrofit = new Retrofit.Builder()//建立Retrofit
                .baseUrl(API_BASE_URL)//连接服务器的基础接口
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//添加Rx适配器
                .addConverterFactory(GsonConverterFactory.create()) // 添加Gson转换器
                .client(okHttpClient)//设置OkHttp
                .build();
        service = retrofit.create(ApiService.class);//为retrofit创建请求服务器的接口.ApiService为一个接口,内部定义了请求服务器的方法
    }

    public static Api getInstance() {//获取单例
        if (instance == null)
            instance = new Api();
        return instance;
    }

    public Observable<BaseData<InitData>> Init(){
        return service.Init();
    }

    public Observable<BaseData<List<String>>> GetAreaDetail(){
        return service.GetAreaDetail();
    }

    public Observable<BaseData<UserData>> Login(List<MultipartBody.Part> parts){
        return service.Login(parts);
    }

    public Observable<BaseData<UserData>> GetUserInfo(List<MultipartBody.Part> parts){
        return service.GetUserInfo(parts);
    }

    public Observable<BaseData<List<ShareListData<List<String>>>>> LoadShareList(List<MultipartBody.Part> parts){
        return service.LoadShareList(parts);
    }

    public  Observable<BaseData<List<MessageListData>>> LoadMessageList(List<MultipartBody.Part> parts){
        return service.LoadMessageList(parts);
    }

    public Observable<BaseData<ShareListData<List<String>>>> PutReMark(List<MultipartBody.Part> parts){
        return  service.PutReMark(parts);
    }

    public Observable<BaseData<UserData>> ChangeUserDetail(List<MultipartBody.Part> parts){
        return service.ChangeUserDetail(parts);
    }

    public Observable<BaseData<String>> UpImg(List<MultipartBody.Part> parts){
        return service.UpImg(parts);
    }

    public Observable<BaseData<String>> UpAudio(@Part List<MultipartBody.Part> parts){
        return service.UpAudio(parts);
    }

    public Observable<BaseData> ShareCommon(List<MultipartBody.Part> parts){
        return service.ShareCommon(parts);
    }

    public Observable<BaseData> ShareDiary(List<MultipartBody.Part> parts){
        return service.ShareDiary(parts);
    }

    public Observable<BaseData> ShareMenu(List<MultipartBody.Part> parts){
        return service.ShareMenu(parts);
    }

    public Observable<BaseData<RaidersDetailData<List<RaidersListData<List<String>>>,List<String>>>> ShareRaiders(List<MultipartBody.Part> parts){
        return service.ShareRaiders(parts);
    }

    public Observable<BaseData<List<MessageListData>>> GetMessageDetail(List<MultipartBody.Part> parts){
        return service.GetMessageDetail(parts);
    }

    public Observable<BaseData<MessageListData>> SendMessage(List<MultipartBody.Part> parts){
        return service.SendMessage(parts);
    }

    public Observable<BaseData> SetMessageRead(List<MultipartBody.Part> parts){
        return service.SetMessageRead(parts);
    }

    public Observable<BaseData<List<CommentData>>> GetComment(List<MultipartBody.Part> parts){
        return service.GetComment(parts);
    }

    public Observable<BaseData<List<CommentData>>> PutComment(List<MultipartBody.Part> parts){
        return service.PutComment(parts);
    }

    public Observable<BaseData<List<ShareListData<List<String>>>>> GetCommentMy(List<MultipartBody.Part> parts){
        return service.GetCommentMy(parts);
    }

    public Observable<BaseData<ShareListData<List<String>>>> GetCommonDetail(List<MultipartBody.Part> parts){
        return service.GetCommonDetail(parts);
    }

    public Observable<BaseData<ShareListData>> GetShareDetail(List<MultipartBody.Part> parts){
        return service.GetShareDetail(parts);
    }

    public  Observable<BaseData<MenuDetailData<List<MenuPracticeData<List<String>>>,List<String>>>> GetMenuDetail(List<MultipartBody.Part> parts){
        return service.GetMenuDetail(parts);
    }

    public Observable<BaseData<RaidersDetailData<List<RaidersListData<List<String>>>,List<String>>>> GetRaidersDetail(List<MultipartBody.Part> parts){
        return service.GetRaidersDetail(parts);
    }

    public Observable<BaseData> Collection(List<MultipartBody.Part> parts){
        return service.Collection(parts);
    }

    public Observable<BaseData<List<ShareListData<List<String>>>>> GetCollection(List<MultipartBody.Part> parts){
        return service.GetCollection(parts);
    }

    public Observable<BaseData<List<ShareListData<List<String>>>>> GetTop(List<MultipartBody.Part> parts){
        return service.GetTop(parts);
    }
    public Observable<BaseData> PutTop(List<MultipartBody.Part> parts){
        return service.PutTop(parts);
    }

    public Observable<BaseData<List<FlowData>>> GetFlow(){
        return service.GetFlow();
    }

    public Observable<BaseData<List<ShareListData<List<String>>>>> Search(List<MultipartBody.Part> parts){
        return service.Search(parts);
    }

    public  Observable<BaseData> Check(List<MultipartBody.Part> parts){
        return service.Check(parts);
    }

    public  Observable<BaseData> Registered(List<MultipartBody.Part> parts){
        return service.Registered(parts);
    }

    public Observable<BaseData> Complaint(List<MultipartBody.Part> parts){
        return service.Complaint(parts);
    }

    public Observable<BaseData> Feedback(List<MultipartBody.Part> parts){
        return service.Feedback(parts);
    }

    public Observable<BaseData> Appeal(List<MultipartBody.Part> parts){
        return service.Appeal(parts);
    }

    public Observable<BaseData<List<ShareListData<List<String>>>>> GetReject(List<MultipartBody.Part> parts){
        return service.GetReject(parts);
    }

    public  Observable<BaseData> RejectDelete(List<MultipartBody.Part> parts){
        return service.RejectDelete(parts);
    }

    public Observable<BaseData<List<HotSearch>>> GetHotSearch(){
        return service.GetHotSearch();
    }

}
