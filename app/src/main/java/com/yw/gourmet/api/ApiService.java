package com.yw.gourmet.api;


import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.data.UserData;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by yw on 2017-08-08.
 *  @Multipart,@POST,@PartMap,@Part
 */

public interface ApiService {
    //登录
    @Multipart
    @POST("/Login")
    Observable<BaseData<UserData>> Login(@Part List<MultipartBody.Part> parts);

    //加载分享列表
    @Multipart
    @POST("/ShareList/Load")
    Observable<BaseData<List<ShareListData<List<String>>>>> LoadShareList(@Part List<MultipartBody.Part> parts);
}
