package com.yw.gourmet.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yw.gourmet.R;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.listener.OnItemClickListener;

import java.util.List;

/**
 * Created by LYW on 2017/11/18.
 */

public class ShareListAdapter extends RecyclerView.Adapter<ShareListAdapter.MyViewHolder>{
    private Context context;
    private List<ShareListData<String>> listData;
    private OnItemClickListener listener;

    public ShareListAdapter setListener(OnItemClickListener listener) {
        this.listener = listener;
        return this;
    }

    public ShareListAdapter(Context context, List<ShareListData<String>> listData){
        this.context = context;
        this.listData = listData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_share_list,parent
                ,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.tv_content.setText(listData.get(position).getContent());
        holder.tv_time.setText(listData.get(position).getPut_time());
        holder.tv_nickname.setText(listData.get(position).getNickname());
        if (listener != null){
            holder.constraint_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnClick(v,holder.getLayoutPosition());
                }
            });
            holder.constraint_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return listener.OnLongClick(v,holder.getLayoutPosition());
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
        MyViewHolder(View itemView) {
            super(itemView);
            constraint_item = (ConstraintLayout)itemView.findViewById(R.id.constraint_item);
            img_header = (ImageView)itemView.findViewById(R.id.img_header);
            tv_nickname = (TextView)itemView.findViewById(R.id.tv_nickname);
            tv_time = (TextView)itemView.findViewById(R.id.tv_time);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }
}
