package com.yw.gourmet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    public static final int NOT_SUPPORT = -1;

    private Context context;
    private int VoiceMaxLength = WindowUtil.width/2;
    private List<MessageListData> list;
    private OnRefreshListener onRefreshListener;
    private OnImgClickListener onImgClickListener;
    private OnVoiceClickListener onVoiceClickListener;
    private UserData getUserData;//获取者的信息(左边)
    private SparseArray<View> sparseArray = new SparseArray<>();

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
        }
        Log.e("TYPE:", String.valueOf(result));
        return result;
    }

    @Override
    public BaseViewHolder<MessageListData> onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder<MessageListData> holder = new MyTextHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_text,parent,false),viewType);
        switch (viewType){
            case TEXT_MY:
                holder =  new MyTextHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_text_myself, parent, false), viewType);
                break;
            case TEXT_OTHER:
                holder =  new OtherTextHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_text_other, parent, false), viewType);
                break;
            case IMG_MY:
                holder =  new MyImgHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_img_myself, parent, false), viewType);
                break;
            case IMG_OTHER:
                holder =  new OtherImgHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_img_other, parent, false), viewType);
                break;
            case VOICE_MY:
                holder =   new MyVoiceHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_voice_myself, parent, false), viewType);
                break;
            case VOICE_OTHER:
                holder =  new OtherVoiceHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_voice_other, parent, false), viewType);
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

    abstract class ChatViewHolder extends BaseViewHolder<MessageListData> {
        ImageView img_header;
        FrameLayout fl_status;

        public ChatViewHolder(View itemView, int type) {
            super(itemView, type);
            init(itemView);
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

        public void init(View itemView){
            img_header = itemView.findViewById(R.id.img_header);
            fl_status = itemView.findViewById(R.id.fl_sending);
        }
    }

    class MyTextHolder extends ChatViewHolder {
        TextView tv_content;

        public MyTextHolder(View itemView, int type) {
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

    class OtherTextHolder extends ChatViewHolder {
        TextView tv_content;

        public OtherTextHolder(View itemView, int type) {
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



    class MyImgHolder extends ChatViewHolder{
        ImageView img;

        public MyImgHolder(View itemView, int type) {
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

    class OtherImgHolder extends ChatViewHolder{
        ImageView img;

        public OtherImgHolder(View itemView, int type) {
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


    class MyVoiceHolder extends ChatViewHolder {
        LinearLayout ll;

        public MyVoiceHolder(View itemView, int type) {
            super(itemView, type);
        }

        @Override
        public void onBind( MessageListData data) {
            super.onBind(data);
            if (data.getPut_id().equals(Constant.userData.getUser_id())) {
                ll.setPadding(VoiceMaxLength * data.getLength() / 60000, ll.getPaddingTop()
                        , ll.getPaddingRight(), ll.getPaddingBottom());
            }else {
                ll.setPadding(ll.getPaddingLeft(), ll.getPaddingTop()
                        , VoiceMaxLength * data.getLength() / 60000, ll.getPaddingBottom());
            }
            if (onVoiceClickListener != null){
                ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onVoiceClickListener.onVoiceClick(v,getLayoutPosition());
                    }
                });
            }
        }

        @Override
        public void init(View itemView) {
            super.init(itemView);
            ll = itemView.findViewById(R.id.ll_voice_my);
        }
    }

    class OtherVoiceHolder extends ChatViewHolder {
        LinearLayout ll;

        public OtherVoiceHolder(View itemView, int type) {
            super(itemView, type);
        }

        @Override
        public void onBind( MessageListData data) {
            super.onBind(data);
            if (data.getPut_id().equals(Constant.userData.getUser_id())) {
                ll.setPadding(VoiceMaxLength * data.getLength() / 60000, ll.getPaddingTop()
                        , ll.getPaddingRight(), ll.getPaddingBottom());
            }else {
                ll.setPadding(ll.getPaddingLeft(), ll.getPaddingTop()
                        , VoiceMaxLength * data.getLength() / 60000, ll.getPaddingBottom());
            }
            if (onVoiceClickListener != null){
                ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onVoiceClickListener.onVoiceClick(v,getLayoutPosition());
                    }
                });
            }
        }

        @Override
        public void init(View itemView) {
            super.init(itemView);
            ll = itemView.findViewById(R.id.ll_voice_other);
        }
    }
}
