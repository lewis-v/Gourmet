package com.yw.gourmet.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.data.MessageListData;
import com.yw.gourmet.listener.OnItemClickListener;

import java.util.List;

/**
 * Created by LYW on 2017/11/25.
 */

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MyViewHolder>{
    private List<MessageListData> listData;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public MessageListAdapter(Context context,List<MessageListData> listData) {
        this.listData = listData;
        this.context = context;
    }

    public MessageListAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message_list
                ,parent,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.tv_time.setText(listData.get(position).getTime());
        holder.tv_content.setText(listData.get(position).getContent());
        holder.tv_nickname.setText(listData.get(position).getNickname());
        GlideApp.with(context).load(listData.get(position).getImg_header()).error(R.mipmap.load_fail)
                .into(holder.img_header);
        if (onItemClickListener != null){
            holder.constraint_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.OnClick(v,holder.getLayoutPosition());
                }
            });
            holder.constraint_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return onItemClickListener.OnLongClick(v,holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout constraint_item;
        ImageView img_header;
        TextView tv_nickname,tv_time,tv_content;
        public MyViewHolder(View itemView) {
            super(itemView);
            constraint_item = (ConstraintLayout)itemView.findViewById(R.id.constraint_item);
            img_header = (ImageView)itemView.findViewById(R.id.img_header);
            tv_nickname = (TextView)itemView.findViewById(R.id.tv_nickname);
            tv_content = (TextView)itemView.findViewById(R.id.tv_content);
            tv_time = (TextView)itemView.findViewById(R.id.tv_time);
        }
    }
}
