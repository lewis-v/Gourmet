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
import com.yw.gourmet.R;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.listener.OnItemClickListener;

import java.util.List;

/**
 * auth: lewis-v
 * time: 2018/1/26.
 */

public class MySetTopAdapter extends RecyclerView.Adapter<MySetTopAdapter.MyViewHolder>{
    private Context context;
    private List<ShareListData<List<String>>> data;
    private OnItemClickListener onItemClickListener;
    private OnTopClickListener onTopClickListener;

    public MySetTopAdapter(Context context, List<ShareListData<List<String>>> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_draft,parent,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        switch (data.get(position).getType()){
            case Constant.TypeFlag.SHARE:
                holder.tv_type.setText("分享");
                holder.tv_title.setText(data.get(position).getContent());
                break;
            case Constant.TypeFlag.DIARY:
                holder.tv_type.setText("日记");
                holder.tv_title.setText(data.get(position).getTitle());
                break;
            case Constant.TypeFlag.MENU:
                holder.tv_type.setText("食谱");
                holder.tv_title.setText(data.get(position).getTitle());
                break;
            case Constant.TypeFlag.RAIDERS:
                holder.tv_type.setText("攻略");
                holder.tv_title.setText(data.get(position).getTitle());
                break;
        }
        holder.tv_time.setText(data.get(position).getPut_time());
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
        if (onTopClickListener != null){
            holder.ll_other.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTopClickListener.onSetTop(v,holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_type,tv_time,tv_title;
        LinearLayout ll_other;
        ConstraintLayout constraint_item;
        ImageView img_other;

        public MyViewHolder(View itemView) {
            super(itemView);
            constraint_item = itemView.findViewById(R.id.constraint_item);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_title = itemView.findViewById(R.id.tv_title);
            ll_other = itemView.findViewById(R.id.ll_other);
            img_other = itemView.findViewById(R.id.img_other);
        }
    }

    public interface OnTopClickListener{
        void onSetTop(View view,int position);
    }

    public MySetTopAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public MySetTopAdapter setOnTopClickListener(OnTopClickListener onTopClickListener) {
        this.onTopClickListener = onTopClickListener;
        return this;
    }
}
