package com.yw.gourmet.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseViewHolder;
import com.yw.gourmet.data.MessageListData;
import com.yw.gourmet.data.UserData;
import com.yw.gourmet.listener.OnRefreshListener;
import com.yw.gourmet.utils.TimeUtils;
import com.yw.gourmet.utils.WindowUtil;

import java.util.List;

/**
 * Created by Lewis-v on 2017/12/29.
 */

public class ChatAdapter extends RecyclerView.Adapter<BaseViewHolder<MessageListData>>{
    public static final int TEXT_MY = 0;
    public static final int IMG_MY = 1;
    public static final int VOICE_MY = 2;
    public static final int TEXT_OTHER = 3;
    public static final int IMG_OTHER = 4;
    public static final int VOICE_OTHER = 5;
    public static final int VOICE_CHAT_MY = 6;//语音聊天
    public static final int VOICE_CHAT_OTHER = 7;//语音聊天
    public static final int NOT_SUPPORT = -1;

    private Context context;
    private int VoiceMaxLength = WindowUtil.width/2;
    private List<MessageListData> list;
    private OnRefreshListener onRefreshListener;
    private OnImgClickListener onImgClickListener;
    private OnVoiceClickListener onVoiceClickListener;
    private UserData getUserData;//获取者的信息(左边)
    private SparseArray<View> sparseArray = new SparseArray<>();
    private String playPath = "";//播放语音的地址

    public ChatAdapter(Context context, List<MessageListData> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        int result = NOT_SUPPORT;
        switch (list.get(position).getType()){
            case MessageListData.TEXT:
                if (isMySelf(list.get(position).getPut_id())) {
                    result =  TEXT_MY;
                }else {
                    result =  TEXT_OTHER;
                }
                break;
            case MessageListData.IMG:
                if (isMySelf(list.get(position).getPut_id())) {
                    result =  IMG_MY;
                }else {
                    result =  IMG_OTHER;
                }
                break;
            case MessageListData.VOICE:
                if (isMySelf(list.get(position).getPut_id())) {
                    result =  VOICE_MY;
                }else {
                    result =  VOICE_OTHER;
                }
                break;
            case MessageListData.VOICE_CHAT:
                if (isMySelf(list.get(position).getPut_id())) {
                    result =  VOICE_CHAT_MY;
                }else {
                    result =  VOICE_CHAT_OTHER;
                }
                break;
        }
        return result;
    }

    @Override
    public BaseViewHolder<MessageListData> onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder<MessageListData> holder = null;
        switch (viewType){
            case TEXT_MY:
                holder =  new MyTextHolderBase(LayoutInflater.from(context).inflate(R.layout.item_chat_text_myself, parent, false), viewType);
                break;
            case TEXT_OTHER:
                holder =  new OtherTextHolderBase(LayoutInflater.from(context).inflate(R.layout.item_chat_text_other, parent, false), viewType);
                break;
            case IMG_MY:
                holder =  new MyImgHolderBase(LayoutInflater.from(context).inflate(R.layout.item_chat_img_myself, parent, false), viewType);
                break;
            case IMG_OTHER:
                holder =  new OtherImgHolderBase(LayoutInflater.from(context).inflate(R.layout.item_chat_img_other, parent, false), viewType);
                break;
            case VOICE_MY:
                holder =   new MyVoiceHolderBase(LayoutInflater.from(context).inflate(R.layout.item_chat_voice_myself, parent, false), viewType);
                break;
            case VOICE_OTHER:
                holder =  new OtherVoiceHolderBase(LayoutInflater.from(context).inflate(R.layout.item_chat_voice_other, parent, false), viewType);
                break;
            case VOICE_CHAT_MY:
                holder =  new MyVoiceChatHolder(LayoutInflater.from(context).inflate(R.layout.item_voice_chat_my, parent, false), viewType);
                break;
            case VOICE_CHAT_OTHER:
                holder =  new OtherVoiceChatHolder(LayoutInflater.from(context).inflate(R.layout.item_voice_chat_other, parent, false), viewType);
                break;
        }
        if (holder == null){
           holder = new MyTextHolderBase(LayoutInflater.from(context).inflate(R.layout.item_chat_text,parent,false),viewType);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder<MessageListData> holder, int position) {
        holder.onBind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public ChatAdapter setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
        return this;
    }

    public ChatAdapter setOnImgClickListener(OnImgClickListener onImgClickListener) {
        this.onImgClickListener = onImgClickListener;
        return this;
    }

    public ChatAdapter setOnVoiceClickListener(OnVoiceClickListener onVoiceClickListener) {
        this.onVoiceClickListener = onVoiceClickListener;
        return this;
    }

    public UserData getGetUserData() {
        return getUserData;
    }

    public ChatAdapter setGetUserData(UserData getUserData) {
        this.getUserData = getUserData;
        return this;
    }

    /**
     * 通过_id获取位置,这个有可能会获取不到
     * @return
     */
    public int getPositionBy_id(long _id){
        int position = 0;
        for (MessageListData data : list){
            if (data.get_id() == _id){
                return position;
            }
            position++;
        }
        return -1;
    }

    public SparseArray<View> getSparseArray() {
        return sparseArray;
    }

    public interface OnImgClickListener{
        void onClick(View view,int position);
    }

    public interface OnVoiceClickListener{
        void onVoiceClick(View view,int position);
    }

    public boolean isMySelf(String id){
        if (id.equals(Constant.userData.getUser_id())){
            return true;
        }
        return false;
    }

    abstract class BaseChatViewHolder extends BaseViewHolder<MessageListData> {
        ImageView img_header;
        FrameLayout fl_status;

        public BaseChatViewHolder(View itemView, int type) {
            super(itemView, type);
        }

        @Override
        public void onBind(MessageListData data) {
            if (data.getPut_id().equals(Constant.userData.getUser_id())) {
                if (Constant.userData.getImg_header() == null) {
                    GlideApp.with(context).load(data.getImg_header()).error(R.mipmap.load_fail)
                            .placeholder(R.mipmap.loading).into(img_header);
                }else {
                    GlideApp.with(context).load(Constant.userData.getImg_header()).error(R.mipmap.load_fail)
                            .placeholder(R.mipmap.loading).into(img_header);
                }
            }else {
                if (getGetUserData() == null) {
                    GlideApp.with(context).load(data.getImg_header()).error(R.mipmap.load_fail)
                            .placeholder(R.mipmap.loading).into(img_header);
                }else {
                    GlideApp.with(context).load(getGetUserData().getImg_header()).error(R.mipmap.load_fail)
                            .placeholder(R.mipmap.loading).into(img_header);
                }
            }
            fl_status.removeAllViews();
            switch (data.getSendStatus()){
                case MessageListData.SENDING:
                    fl_status.addView(new ProgressBar(context));
                    fl_status.setOnClickListener(null);
                    break;
                case MessageListData.SEND_SUCCESS:
                    fl_status.setOnClickListener(null);
                    break;
                case MessageListData.SEND_FAIL:
                    ImageView imageView = new ImageView(context);
                    imageView.setImageResource(R.drawable.refresh);
                    fl_status.addView(imageView);
                    if (onRefreshListener != null){
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onRefreshListener.OnRefresh(v,getLayoutPosition());
                            }
                        });
                    }
                    break;
            }
        }
        @Override
        public void init(View itemView){
            img_header = itemView.findViewById(R.id.img_header);
            fl_status = itemView.findViewById(R.id.fl_sending);
        }
    }

    class MyTextHolderBase extends BaseChatViewHolder {
        TextView tv_content;

        public MyTextHolderBase(View itemView, int type) {
            super(itemView, type);
        }

        @Override
        public void onBind(MessageListData data) {
            super.onBind(data);
            tv_content.setText(data.getContent());
        }

        @Override
        public void init(View itemView) {
            super.init(itemView);
            tv_content = itemView.findViewById(R.id.tv_myself);
        }
    }

    class OtherTextHolderBase extends BaseChatViewHolder {
        TextView tv_content;

        public OtherTextHolderBase(View itemView, int type) {
            super(itemView, type);
        }

        @Override
        public void onBind(MessageListData data) {
            super.onBind(data);
            tv_content.setText(data.getContent());
        }

        @Override
        public void init(View itemView) {
            super.init(itemView);
            tv_content = itemView.findViewById(R.id.tv_other);
        }
    }



    class MyImgHolderBase extends BaseChatViewHolder {
        ImageView img;

        public MyImgHolderBase(View itemView, int type) {
            super(itemView, type);
        }

        @Override
        public void onBind(MessageListData data) {
            super.onBind(data);
            GlideApp.with(context).load(data.getImg()).error(R.mipmap.load_fail)
                    .placeholder(R.mipmap.loading).into(img);
            if (onImgClickListener != null){
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onImgClickListener.onClick(v,getLayoutPosition());
                    }
                });
            }
        }

        @Override
        public void init(View itemView) {
            super.init(itemView);
            img = itemView.findViewById(R.id.img_myself);
        }
    }

    class OtherImgHolderBase extends BaseChatViewHolder {
        ImageView img;

        public OtherImgHolderBase(View itemView, int type) {
            super(itemView, type);
        }

        @Override
        public void onBind(MessageListData data) {
            super.onBind(data);
            GlideApp.with(context).load(data.getImg()).error(R.mipmap.load_fail)
                    .placeholder(R.mipmap.loading).into(img);
            if (onImgClickListener != null){
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onImgClickListener.onClick(v,getLayoutPosition());
                    }
                });
            }
        }

        @Override
        public void init(View itemView) {
            super.init(itemView);
            img = itemView.findViewById(R.id.img_other);
        }
    }


    class MyVoiceHolderBase extends BaseChatViewHolder {
        LinearLayout ll;
        ImageView img_voice_my;

        public MyVoiceHolderBase(View itemView, int type) {
            super(itemView, type);
        }

        @Override
        public void onBind( MessageListData data) {
            super.onBind(data);
            ll.setPadding(VoiceMaxLength * data.getLength() / 60000, ll.getPaddingTop()
                    , ll.getPaddingRight(), ll.getPaddingBottom());
            if (onVoiceClickListener != null){
                ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onVoiceClickListener.onVoiceClick(v,getLayoutPosition());
                    }
                });
            }
            if (data.getSendStatus() != MessageListData.SEND_FAIL
                    && data.getSendStatus() != MessageListData.SENDING){
                TextView tv_length = new TextView(context);
                tv_length.setText(data.getLength()/1000+"\"");
                fl_status.addView(tv_length);
            }
            if (data.getImg().equals(playPath)){
                img_voice_my.setImageResource(R.drawable.anim_voice_play);
                ((AnimationDrawable)img_voice_my.getDrawable()).start();
            }else {
//                ((AnimationDrawable)img_voice_my.getDrawable()).destroy();
                img_voice_my.setImageResource(R.drawable.voice_item);
            }
        }

        @Override
        public void init(View itemView) {
            super.init(itemView);
            ll = itemView.findViewById(R.id.ll_voice_my);
            img_voice_my = itemView.findViewById(R.id.img_voice_my);
        }
    }

    class OtherVoiceHolderBase extends BaseChatViewHolder {
        LinearLayout ll;
        ImageView img_voice_other;

        public OtherVoiceHolderBase(View itemView, int type) {
            super(itemView, type);
        }

        @Override
        public void onBind( MessageListData data) {
            super.onBind(data);
            ll.setPadding(ll.getPaddingLeft(), ll.getPaddingTop()
                    , VoiceMaxLength * data.getLength() / 60000, ll.getPaddingBottom());
            if (onVoiceClickListener != null){
                ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onVoiceClickListener.onVoiceClick(v,getLayoutPosition());
                    }
                });
            }
            if (data.getSendStatus() != MessageListData.SEND_FAIL
                    && data.getSendStatus() != MessageListData.SENDING){
                TextView tv_length = new TextView(context);
                tv_length.setText(data.getLength()/1000+"\"");
                fl_status.addView(tv_length);
            }
            if (data.getImg().equals(playPath)){
                img_voice_other.setImageResource(R.drawable.anim_voice_play);
                ((AnimationDrawable)img_voice_other.getDrawable()).start();
            }else {
//                ((AnimationDrawable)img_voice_my.getDrawable()).destroy();
                img_voice_other.setImageResource(R.drawable.voice_item);
            }
        }

        @Override
        public void init(View itemView) {
            super.init(itemView);
            ll = itemView.findViewById(R.id.ll_voice_other);
            img_voice_other = itemView.findViewById(R.id.img_voice_other);
        }
    }

    class MyVoiceChatHolder extends BaseChatViewHolder{
        TextView tv_content;

        public MyVoiceChatHolder(View itemView, int type) {
            super(itemView, type);
        }

        @Override
        public void onBind(MessageListData data) {
            super.onBind(data);
            if (data.getLength()>0){
                tv_content.setText("聊天时长  "+ TimeUtils.getVoiceChatLength(data.getLength()));
            }else if (data.getImg() != null && data.getImg().length() > 0){
                tv_content.setText(data.getImg());
            }else {
                tv_content.setText("语聊取消");
            }
        }

        @Override
        public void init(View itemView) {
            super.init(itemView);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }

    class OtherVoiceChatHolder extends BaseChatViewHolder{
        TextView tv_content;

        public OtherVoiceChatHolder(View itemView, int type) {
            super(itemView, type);
        }

        @Override
        public void onBind(MessageListData data) {
            super.onBind(data);
            if (data.getLength()>0){
                tv_content.setText("聊天时长  "+ TimeUtils.getVoiceChatLength(data.getLength()));
            }else if (data.getImg() != null && data.getImg().length() > 0){
                tv_content.setText(data.getImg());
            }else {
                tv_content.setText("语聊取消");
            }
        }

        @Override
        public void init(View itemView) {
            super.init(itemView);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }

    public void voiceStop(){
        final int cachePosition = getPositionByImg(playPath);
        playPath = "";
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(cachePosition);
            }
        });
    }

    public void voicePlay(String path){
        final int cachePosition = getPositionByImg(playPath);
        playPath = path;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(cachePosition);
                notifyItemChanged(getPositionByImg(playPath));
            }
        });
    }

    public int getPositionByImg(String img){
        int position = 0;
        for (MessageListData data : list){
            if (data.getImg() != null && data.getImg().equals(img)){
                return position;
            }
            position++;
        }
        return -1;
    }


}
