package com.yw.gourmet.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.data.MessageListData;

import java.util.List;

/**
 * Created by Lewis-v on 2017/12/29.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder>{
    private Context context;
    private List<MessageListData> list;

    public ChatAdapter(Context context, List<MessageListData> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat,parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (Constant.userData != null){
            if (Constant.userData.getId().equals(list.get(position).getId())){//自己发送的
                holder.constraint_other.setVisibility(View.GONE);
                holder.constraint_myself.setVisibility(View.VISIBLE);
                GlideApp.with(context).load(list.get(position).getImg_header()).error(R.mipmap.load_fail)
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
                        GlideApp.with(context).load(list.get(position).getImg()).error(R.mipmap.load_fail)
                                .into(holder.img_myself);
                        break;
                }

            }else {//别人发送的
                holder.constraint_other.setVisibility(View.VISIBLE);
                holder.constraint_myself.setVisibility(View.GONE);
                GlideApp.with(context).load(list.get(position).getImg_header()).error(R.mipmap.load_fail)
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
                        GlideApp.with(context).load(list.get(position).getImg()).error(R.mipmap.load_fail)
                                .into(holder.img_other);
                        break;
                }

            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_item;
        ConstraintLayout constraint_other,constraint_myself;
        ImageView img_header_other,img_header_myself,img_other,img_myself;
        TextView tv_nickname,tv_other,tv_myself;

        public MyViewHolder(View itemView) {
            super(itemView);
            ll_item = itemView.findViewById(R.id.ll_item);
            constraint_myself = itemView.findViewById(R.id.constraint_myself);
            constraint_other = itemView.findViewById(R.id.constraint_other);
            img_header_myself = itemView.findViewById(R.id.img_header_myself);
            img_header_other = itemView.findViewById(R.id.img_header_other);
            img_other = itemView.findViewById(R.id.img_other);
            img_myself = itemView.findViewById(R.id.img_myself);
            tv_myself = itemView.findViewById(R.id.tv_myself);
            tv_nickname = itemView.findViewById(R.id.tv_nickname);
            tv_other = itemView.findViewById(R.id.tv_other);
        }
    }
}
