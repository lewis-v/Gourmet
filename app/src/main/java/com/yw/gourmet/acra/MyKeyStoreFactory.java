package com.yw.gourmet.acra;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.yw.gourmet.Constant;
import com.yw.gourmet.base.ActivityStack;
import com.yw.gourmet.push.PushManager;
import com.yw.gourmet.rxbus.EventSticky;
import com.yw.gourmet.rxbus.RxBus;
import com.yw.gourmet.utils.SPUtils;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.acra.sender.SenderService;
import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * Created by ude on 2017-07-28.
 */

public class MyKeyStoreFactory implements ReportSender {
    /**
     * Send crash report data.
     * <p>
     * Method will be called from the {@link SenderService}.
     *
     * @param context      Android Context in which to send the crash report.
     * @param errorContent Stores key/value pairs for each report field.
     *                     A report field is identified by a {@link ReportField} enum value.
     * @throws ReportSenderException If anything goes fatally wrong during the handling of crash
     *                               data, you can (should) throw a {@link ReportSenderException}
     *                               with a custom message.
     */
    @Override
    public void send(@NonNull Context context, @NonNull CrashReportData errorContent) throws ReportSenderException {
        String content = "------------start-----------------\n"
                + errorContent.getProperty(ReportField.USER_CRASH_DATE) + "\n"
                + errorContent.getProperty(ReportField.BUILD) + "\n"
                + errorContent.getProperty(ReportField.DUMPSYS_MEMINFO) + "\n"
                + errorContent.getProperty(ReportField.STACK_TRACE) + "\n";
        content = content + "------------end-----------------\n";
        final MultipartBody.Builder requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("content",content)
                .addFormDataPart("time",String.valueOf(System.currentTimeMillis()+ Constant.serviceTime));
        if (Constant.userData != null){
            requestBody.addFormDataPart("id",Constant.userData.getUser_id());
        }

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();//打印出请求的信息的拦截器
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);//设置打印请求的记录级别  NONE:不记录; BASIC:请求/响应行; HEADER:请求/响应行+请求头; BODY:请求/响应行+请求头+请求体(所有信息)
        httpClient.addInterceptor(interceptor);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                String head = "head";//头部
                //这里添加头部,这里可以用addHeader来添加多个头部,如果使用header方法就只能添加一个头部
                Request.Builder requestBuilder = original.newBuilder()//添加头部信息
                        .addHeader("head", head);
                Request request = requestBuilder.build();//用设置好的requestBuilder建立一个新的request
                return chain.proceed(request);
            }
        });

        httpClient.retryOnConnectionFailure(true)//设置是否失败重连
                .connectTimeout(20 * 1000, TimeUnit.MILLISECONDS)//连接超时间
                .readTimeout(20 * 1000, TimeUnit.MILLISECONDS)//读超时
                .writeTimeout(20 * 1000, TimeUnit.MILLISECONDS);//写超时
        Call call = httpClient.build().newCall(new Request.Builder().url("http://192.168.31.6:47423/Crash").post(requestBody.build()).build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("---bug---",response.body().toString());
            }
        });
        //重启APP
        Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
        ActivityStack.getScreenManager().clearAllActivity();
    }
}
