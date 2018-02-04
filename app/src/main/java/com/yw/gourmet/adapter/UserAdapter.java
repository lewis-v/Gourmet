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
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.data.UserData;
import com.yw.gourmet.listener.OnItemClickListener;
import com.yw.gourmet.widget.GlideCircleTransform;

import java.util.List;

/**
 * auth: lewis-v
 * time: 2018/2/4.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder>{
    private Context context;
    private List<ShareListData> listData;
    private OnItemClickListener onItemClickListener;

    public UserAdapter(Context context, List<ShareListData> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_user,parent,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.tv_nickname.setText(listData.get(position).getNickname());
        GlideApp.with(context).load(listData.get(position).getImg_header())
                .transform(new GlideCircleTransform(context)).error(R.mipmap.load_fail)
                .placeholder(R.mipmap.loading).into(holder.img_header);
        if (onItemClickListener!=null){
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

    class MyViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraint_item;
        ImageView img_header;
        TextView tv_nickname;

        public MyViewHolder(View itemView) {
            super(itemView);
            img_header = itemView.findViewById(R.id.img_header);
            tv_nickname = itemView.findViewById(R.id.tv_nickname);
            constraint_item = itemView.findViewById(R.id.constraint_item);
        }
    }

    public UserAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }
}
