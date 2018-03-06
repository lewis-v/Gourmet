package com.yw.gourmet.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.dao.data.saveData.SaveData;
import com.yw.gourmet.listener.OnItemClickListener;
import com.yw.gourmet.listener.OnOtherClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * auth: lewis-v
 * time: 2018/1/23.
 */

public class DraftAdapter extends RecyclerView.Adapter<DraftAdapter.MyViewHolder>{
    private Context context;
    private List<SaveData> data;
    private OnItemClickListener onItemClickListener;
    private OnOtherClickListener onOtherClickListener;

    public DraftAdapter(Context context, List<SaveData> data) {
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
        if (data.get(position).getType() >= Constant.TypeFlag.OTHER){
            holder.tv_type.setText("不支持类型");
            holder.tv_title.setText("此版本不支持此类型,请更新APP");
        }
        holder.tv_time.setText(new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date(data.get(position).getChange_time())));
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
        if (onOtherClickListener != null){
            holder.ll_other.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOtherClickListener.onOther(v,holder.getLayoutPosition());
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

        public MyViewHolder(View itemView) {
            super(itemView);
            constraint_item = itemView.findViewById(R.id.constraint_item);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_title = itemView.findViewById(R.id.tv_title);
            ll_other = itemView.findViewById(R.id.ll_other);
        }
    }

    public DraftAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public DraftAdapter setOnOtherClickListener(OnOtherClickListener onOtherClickListener) {
        this.onOtherClickListener = onOtherClickListener;
        return this;
    }
}
