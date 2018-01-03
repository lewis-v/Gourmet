package com.yw.gourmet.dialog;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yw.gourmet.Constant;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseDialogFragment;
import com.yw.gourmet.listener.OnCancelClickListener;
import com.yw.gourmet.ui.wxapi.WXUtils;

/**
 * Created by Lewis-v on 2018/1/3.
 */

public class MyDialogMoreFragment extends BaseDialogFragment implements View.OnClickListener{
    private TextView tv_cancel;
    private LinearLayout ll_collection,ll_share_people,ll_share_friend;
    private OnCancelClickListener onCancelClickListener;
    private OnCollectionListener onCollectionListener;
    private String shareCoverUrl;//分享封面
    private String shareUrl;//分享网页url
    private String shareTitle;//分享标题
    private String shareDescription;//分享简介
    private String id;//操作的id

    @Override
    protected void initView() {
        tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(this);

        ll_collection = view.findViewById(R.id.ll_collection);
        ll_share_friend = view.findViewById(R.id.ll_share_friend);
        ll_share_people = view.findViewById(R.id.ll_share_people);

        ll_collection.setOnClickListener(this);
        ll_share_friend.setOnClickListener(this);
        ll_share_people.setOnClickListener(this);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_dialog_more;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                if (onCancelClickListener != null){
                    onCancelClickListener.OnClick(getTag());
                }
                dismiss();
                break;
            case R.id.ll_collection://收藏
                if (onCollectionListener != null){
                    onCollectionListener.OnCollection(id,getTag());
                }
                break;
            case R.id.ll_share_people://分享微信好友
                shareWX(false);
                break;
            case R.id.ll_share_friend://分享朋友圈
                shareWX(true);
                break;
        }
    }

    /**
     * 分享至微信
     * @param isFriend 是否为朋友圈
     */
    public void shareWX(final boolean isFriend){
        GlideApp.with(this).asBitmap().load(shareCoverUrl).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                IWXAPI api = WXAPIFactory.createWXAPI(getContext(), Constant.APP_ID,true);
                WXWebpageObject wxWebpageObject = new WXWebpageObject();
                wxWebpageObject.webpageUrl = shareUrl;
                WXMediaMessage msg = new WXMediaMessage(wxWebpageObject);
                msg.title = shareTitle;
                msg.description = shareDescription;
//                msg.thumbData = WXUtils.bmpToByteArray(resource,false);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = WXUtils.buildTransaction("webpage");
                req.message = msg;
                req.scene = isFriend ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
                api.sendReq(req);
            }
        });
    }

    public interface OnCollectionListener{
        void OnCollection(String id,String tag);
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
}
