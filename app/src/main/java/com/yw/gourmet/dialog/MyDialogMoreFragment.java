package com.yw.gourmet.dialog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.base.BaseDialogFragment;
import com.yw.gourmet.listener.OnCancelClickListener;
import com.yw.gourmet.ui.wxapi.WXUtils;
import com.yw.gourmet.utils.ToastUtils;

import java.io.IOException;
import java.net.URL;

import okhttp3.MultipartBody;

/**
 * Created by Lewis-v on 2018/1/3.
 */

public class MyDialogMoreFragment extends BaseDialogFragment<MyDIalogPresenter>
        implements MyDialogContract.View,View.OnClickListener{
    private TextView tv_cancel,share_text,tv_collect;
    private LinearLayout ll_collection,ll_share_people,ll_share_friend;
    private OnCancelClickListener onCancelClickListener;
    private OnCollectionListener onCollectionListener;
    private String shareCoverUrl;//分享封面
    private String shareUrl;//分享网页url
    private String shareTitle;//分享标题
    private String shareDescription;//分享简介
    private String id;//操作的id
    private int type;//类型
    private boolean isShare = true;//是否显示分享
    private boolean isShowCollect = true;//是否显示收藏
    private boolean isCollected = false;//是否已经收藏,默认无
    private int position = 0;//操作信息的位置

    @Override
    protected void initView() {
        share_text = view.findViewById(R.id.share_text);
        tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_collect = view.findViewById(R.id.tv_collect);
        tv_cancel.setOnClickListener(this);

        ll_collection = view.findViewById(R.id.ll_collection);
        ll_share_friend = view.findViewById(R.id.ll_share_friend);
        ll_share_people = view.findViewById(R.id.ll_share_people);

        ll_collection.setOnClickListener(this);
        ll_share_friend.setOnClickListener(this);
        ll_share_people.setOnClickListener(this);

        if (!isShowCollect){
            ll_collection.setVisibility(View.GONE);
        }
        if (!isShare){
            ll_share_people.setVisibility(View.GONE);
            ll_share_friend.setVisibility(View.GONE);
            share_text.setVisibility(View.GONE);
        }
        if (isCollected){
            tv_collect.setText("取消收藏");
        }else {
            tv_collect.setText("收藏");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_dialog_more;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                if (onCancelClickListener != null) {
                    onCancelClickListener.OnCancel(getTag());
                }
                dismiss();
                break;
            case R.id.ll_collection://收藏
                if (Constant.userData == null){
                    ToastUtils.showSingleToast("请登录后在进行操作");
                    break;
                }
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("act",isCollected?"-1":"1")
                        .addFormDataPart("act_id",id)
                        .addFormDataPart("type",String.valueOf(type))
                        .addFormDataPart("id",Constant.userData.getId());
                mPresenter.collection(builder.build().parts());
                break;
            case R.id.ll_share_people://分享微信好友
                ((BaseActivity)getActivity()).setLoadDialog(true);
                shareWX(false);
                break;
            case R.id.ll_share_friend://分享朋友圈
                ((BaseActivity)getActivity()).setLoadDialog(true);
                shareWX(true);
                break;
        }
    }

    /**
     * 分享至微信
     * @param isFriend 是否为朋友圈
     */
    public void shareWX(final boolean isFriend){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("---share---",shareCoverUrl);
                IWXAPI api = WXAPIFactory.createWXAPI(getContext(), Constant.APP_ID,true);
                WXWebpageObject wxWebpageObject = new WXWebpageObject();
                wxWebpageObject.webpageUrl = shareUrl;
                WXMediaMessage msg = new WXMediaMessage(wxWebpageObject);
                msg.title = shareTitle;
                msg.description = shareDescription;
                Bitmap bmp = null;
                try {
                    bmp = BitmapFactory.decodeStream(new URL(shareCoverUrl).openStream());
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 100, 100, true);
                bmp.recycle();
                msg.thumbData = WXUtils.bmpToByteArray(thumbBmp,true);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = WXUtils.buildTransaction("webpage");
                req.message = msg;
                req.scene = isFriend ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
                api.sendReq(req);

                ll_collection.post(new Runnable() {
                    @Override
                    public void run() {
                        ((BaseActivity)getActivity()).setLoadDialog(false);
                        MyDialogMoreFragment.this.dismiss();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onFail(String msg) {
        ToastUtils.showSingleToast(msg);
        if (onCollectionListener != null) {
            onCollectionListener.OnCollection(null,type,position, getTag());
        }
        dismiss();
    }

    @Override
    public void onSuccess(String msg) {
        ToastUtils.showSingleToast(msg);
        if (onCollectionListener != null) {
            onCollectionListener.OnCollection(id,type,position, getTag());
        }
        dismiss();
    }

    @Override
    public void onReLoginFail(String msg) {

    }

    @Override
    public void setLoadDialog(boolean isLoadDialog) {

    }

    public interface OnCollectionListener{
        void OnCollection(String id,int type,int position,String tag);
    }

    public MyDialogMoreFragment setOnCancelClickListener(OnCancelClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
        return this;
    }

    public MyDialogMoreFragment setOnCollectionListener(OnCollectionListener onCollectionListener) {
        this.onCollectionListener = onCollectionListener;
        return this;
    }

    public MyDialogMoreFragment setShareCoverUrl(String shareCoverUrl) {
        this.shareCoverUrl = shareCoverUrl;
        return this;
    }

    public MyDialogMoreFragment setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
        return this;
    }

    public MyDialogMoreFragment setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
        return this;
    }

    public MyDialogMoreFragment setId(String id) {
        this.id = id;
        return this;
    }

    public MyDialogMoreFragment setShareDescription(String shareDescription) {
        this.shareDescription = shareDescription;
        return this;
    }

    public MyDialogMoreFragment setShare(boolean share) {
        isShare = share;
        return this;
    }

    public MyDialogMoreFragment setShowCollect(boolean showCollect) {
        isShowCollect = showCollect;
        return this;
    }

    public MyDialogMoreFragment setType(int type) {
        this.type = type;
        return this;
    }

    public MyDialogMoreFragment setCollected(boolean collected) {
        isCollected = collected;
        return this;
    }

    public MyDialogMoreFragment setPosition(int position) {
        this.position = position;
        return this;
    }
}
