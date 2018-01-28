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
import com.yw.gourmet.listener.OnMoveListener;

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
    private OnMoveListener onMoveListener;
    private int movePosition = -1;//移动的位置

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

        if (onMoveListener != null){
            if (1==data.size()){
                holder.img_down.setVisibility(View.GONE);
                holder.img_up.setVisibility(View.GONE);
            }else if (position == 0){
                holder.img_up.setVisibility(View.GONE);
                holder.img_down.setVisibility(View.VISIBLE);
            }else if (position == data.size() -1){
                holder.img_down.setVisibility(View.GONE);
                holder.img_up.setVisibility(View.VISIBLE);
            }else {
                holder.img_up.setVisibility(View.VISIBLE);
                holder.img_down.setVisibility(View.VISIBLE);
            }

            if (movePosition == position){
                holder.ll_change.setVisibility(View.VISIBLE);
            }else {
                holder.ll_change.setVisibility(View.GONE);
            }

            holder.img_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMoveListener.onUp(v,holder.getLayoutPosition());
                }
            });
            holder.img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMoveListener.onDelete(v,holder.getLayoutPosition());
                }
            });
            holder.img_down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMoveListener.onDown(v,holder.getLayoutPosition());
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
        LinearLayout ll_other,ll_change;
        ConstraintLayout constraint_item;
        ImageView img_other,img_up,img_delete,img_down;

        public MyViewHolder(View itemView) {
            super(itemView);
            constraint_item = itemView.findViewById(R.id.constraint_item);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_title = itemView.findViewById(R.id.tv_title);
            ll_other = itemView.findViewById(R.id.ll_other);
            ll_change = itemView.findViewById(R.id.ll_change);
            img_other = itemView.findViewById(R.id.img_other);
            img_delete = itemView.findViewById(R.id.img_delete);
            img_up = itemView.findViewById(R.id.img_up);
            img_down = itemView.findViewById(R.id.img_down);
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

    public MySetTopAdapter setOnMoveListener(OnMoveListener onMoveListener) {
        this.onMoveListener = onMoveListener;
        return this;
    }

    /**
     * 设置move模式的位置
     * @param movePosition
     */
    public void setMove(int movePosition){
        if (movePosition == this.movePosition){
            this.movePosition = -1;
        }else {
            int position = this.movePosition;
            this.movePosition = movePosition;
            if (position >= 0) {
                notifyItemChanged(position);
            }
        }
        notifyItemChanged(movePosition);
    }
}
