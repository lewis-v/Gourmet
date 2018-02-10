package com.yw.gourmet.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.yw.gourmet.data.MessageListData;
import com.yw.gourmet.listener.OnRefreshListener;
import com.yw.gourmet.widget.GlideCircleTransform;

import java.util.List;

/**
 * Created by Lewis-v on 2017/12/29.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder>{
    private Context context;
    private List<MessageListData> list;
    private OnRefreshListener onRefreshListener;
    private OnImgClickListener onImgClickListener;

    public ChatAdapter(Context context, List<MessageListData> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat,parent,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (Constant.userData != null){
            if (Constant.userData.getUser_id().equals(list.get(position).getPut_id())){//自己发送的
                holder.ll_other.setVisibility(View.GONE);
                holder.ll_myself.setVisibility(View.VISIBLE);
                GlideApp.with(context).load(list.get(position).getImg_header())
                        .placeholder(R.mipmap.loading).error(R.mipmap.load_fail)
                        .transform(new GlideCircleTransform(context))
                        .into(holder.img_header_myself);
                switch (list.get(position).getType()) {
                    case MessageListData.TEXT:
                        holder.tv_myself.setVisibility(View.VISIBLE);
                        holder.img_myself.setVisibility(View.GONE);
                        holder.tv_myself.setText(list.get(position).getContent());
                        break;
                    case MessageListData.VOICE:

                        break;
                    case MessageListData.IMG:
                        holder.tv_myself.setVisibility(View.GONE);
                        holder.img_myself.setVisibility(View.VISIBLE);
                        GlideApp.with(context).load(list.get(position).getImg())
                                .placeholder(R.mipmap.loading).error(R.mipmap.load_fail)
                                .into(holder.img_myself);
                        if (onImgClickListener != null){
                            holder.img_myself.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onImgClickListener.onClick(v,holder.getLayoutPosition());
                                }
                            });
                        }
                        break;
                }
                holder.fl_sending_myself.removeAllViews();
                switch (list.get(position).getSendStatus()) {
                    case MessageListData.SENDING:
                        holder.fl_sending_myself.setVisibility(View.VISIBLE);
                        holder.fl_sending_myself.setOnClickListener(null);
                        holder.fl_sending_myself.addView(new ProgressBar(context));
                        break;
                    case MessageListData.SEND_SUCCESS:
                        holder.fl_sending_myself.setVisibility(View.INVISIBLE);
                        holder.fl_sending_myself.setOnClickListener(null);
                        break;
                    case MessageListData.SEND_FAIL:
                        holder.fl_sending_myself.setVisibility(View.VISIBLE);
                        ImageView imageViewMyself = new ImageView(context);
                        imageViewMyself.setImageResource(R.drawable.refresh);
                        holder.fl_sending_myself.addView(imageViewMyself);
                        if (onRefreshListener != null){
                            holder.fl_sending_myself.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onRefreshListener.OnRefresh(v,holder.getLayoutPosition());
                                }
                            });
                        }
                        break;
                }

            }else {//别人发送的
                holder.ll_other.setVisibility(View.VISIBLE);
                holder.ll_myself.setVisibility(View.GONE);
                GlideApp.with(context).load(list.get(position).getImg_header())
                        .placeholder(R.mipmap.loading).error(R.mipmap.load_fail)
                        .transform(new GlideCircleTransform(context))
                        .into(holder.img_header_other);
                switch (list.get(position).getType()) {
                    case MessageListData.TEXT:
                        holder.tv_other.setVisibility(View.VISIBLE);
                        holder.img_other.setVisibility(View.GONE);
                        holder.tv_other.setText(list.get(position).getContent());
                        break;
                    case MessageListData.VOICE:

                        break;
                    case MessageListData.IMG:
                        holder.tv_other.setVisibility(View.GONE);
                        holder.tv_other.setVisibility(View.VISIBLE);
                        GlideApp.with(context).load(list.get(position).getImg())
                                .placeholder(R.mipmap.loading).error(R.mipmap.load_fail)
                                .into(holder.img_other);
                        if (onImgClickListener != null){
                            holder.img_other.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onImgClickListener.onClick(v,holder.getLayoutPosition());
                                }
                            });
                        }
                        break;
                }
                holder.fl_sending_other.removeAllViews();
                switch (list.get(position).getSendStatus()) {
                    case MessageListData.SENDING:
                        holder.fl_sending_other.setVisibility(View.VISIBLE);
                        holder.fl_sending_other.setOnClickListener(null);
                        holder.fl_sending_other.addView(new ProgressBar(context));
                        break;
                    case MessageListData.SEND_SUCCESS:
                        holder.fl_sending_other.setVisibility(View.INVISIBLE);
                        holder.fl_sending_other.setOnClickListener(null);
                        break;
                    case MessageListData.SEND_FAIL:
                        holder.fl_sending_other.setVisibility(View.VISIBLE);
                        ImageView imageViewMyself = new ImageView(context);
                        imageViewMyself.setImageResource(R.drawable.refresh);
                        holder.fl_sending_other.addView(imageViewMyself);
                        if (onRefreshListener != null){
                            holder.fl_sending_other.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onRefreshListener.OnRefresh(v,holder.getLayoutPosition());
                                }
                            });
                        }
                        break;
                }
            }
        }
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

    public interface OnImgClickListener{
        void onClick(View view,int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_item;
        LinearLayout ll_other,ll_myself;
        ImageView img_header_other,img_header_myself,img_other,img_myself;
        TextView tv_nickname,tv_other,tv_myself;
        FrameLayout fl_sending_other,fl_sending_myself;

        public MyViewHolder(View itemView) {
            super(itemView);
            ll_item = itemView.findViewById(R.id.ll_item);
            ll_myself = itemView.findViewById(R.id.ll_myself);
            ll_other = itemView.findViewById(R.id.ll_other);
            img_header_myself = itemView.findViewById(R.id.img_header_myself);
            img_header_other = itemView.findViewById(R.id.img_header_other);
            img_other = itemView.findViewById(R.id.img_other);//发送的图像
            img_myself = itemView.findViewById(R.id.img_myself);
            tv_myself = itemView.findViewById(R.id.tv_myself);
            tv_nickname = itemView.findViewById(R.id.tv_nickname);
            tv_other = itemView.findViewById(R.id.tv_other);
            fl_sending_other = itemView.findViewById(R.id.fl_sending_other);
            fl_sending_myself = itemView.findViewById(R.id.fl_sending_myself);
        }
    }
}
