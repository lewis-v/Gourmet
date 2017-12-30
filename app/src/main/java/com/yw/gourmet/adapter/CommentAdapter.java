package com.yw.gourmet.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.data.CommentData;

import java.util.List;

/**
 * Created by Lewis-v on 2017/12/30.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder>{
    private Context context;
    private List<CommentData> list;

    public CommentAdapter(Context context, List<CommentData> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_comment,parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GlideApp.with(context).load(list.get(position).getImg_header()).error(R.mipmap.load_fail).into(holder.img_header);
        holder.tv_nickname.setText(list.get(position).getNickname());
        holder.tv_time.setText(list.get(position).getCreate_time());
        holder.tv_content.setText(list.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout constraint_item;
        ImageView img_header;
        TextView tv_nickname,tv_time,tv_content;

        public MyViewHolder(View itemView) {
            super(itemView);
            constraint_item = itemView.findViewById(R.id.constraint_item);
            img_header = itemView.findViewById(R.id.img_header);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_nickname = itemView.findViewById(R.id.tv_nickname);
        }
    }
}
