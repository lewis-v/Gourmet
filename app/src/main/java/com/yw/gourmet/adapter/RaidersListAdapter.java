package com.yw.gourmet.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.data.RaidersListData;
import com.yw.gourmet.listener.OnAddListener;

import java.util.List;

/**
 * auth: lewis-v
 * time: 2018/1/8.
 */

public class RaidersListAdapter extends RecyclerView.Adapter<RaidersListAdapter.MyViewHolder>{
    private Context context;
    private List<RaidersListData<List<String>>> listData;
    private boolean isChange = false;//是否可以修改,默认不可以修改
    private OnAddListener onAddListener;

    public RaidersListAdapter(Context context, List<RaidersListData<List<String>>> listData) {
        this.context = context;
        this.listData = listData;
    }

    public RaidersListAdapter(Context context, List<RaidersListData<List<String>>> listData, boolean isChange) {
        this.context = context;
        this.listData = listData;
        this.isChange = isChange;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_raiders_list,parent,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (isChange){
            if (position >= listData.size()){
                holder.ll_add.setVisibility(View.VISIBLE);
                holder.ll_left.setVisibility(View.GONE);
                holder.constraint_item.setVisibility(View.GONE);
                if (onAddListener != null){
                    holder.ll_add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onAddListener.OnAdd(v,holder.getLayoutPosition());
                        }
                    });
                }
            }else {
                if (position == 0){
                    holder.tv_top.setVisibility(View.INVISIBLE);
                    holder.tv_bottom.setVisibility(View.VISIBLE);
                }else if (position == listData.size() - 1){
                    holder.tv_top.setVisibility(View.VISIBLE);
                    holder.tv_bottom.setVisibility(View.INVISIBLE);
                }else {
                    holder.tv_top.setVisibility(View.VISIBLE);
                    holder.tv_bottom.setVisibility(View.VISIBLE);
                }
                holder.ll_add.setVisibility(View.GONE);
                holder.ll_left.setVisibility(View.VISIBLE);
                holder.constraint_item.setVisibility(View.VISIBLE);
                holder.tv_title.setText(listData.get(position).getTitle());
//                holder.tv_type.setText(listData.get(position).getType().toString());
                holder.tv_address.setText(listData.get(position).getAddress());
                GlideApp.with(context).load(listData.get(position).getImg_cover())
                        .error(R.mipmap.load_fail).into(holder.img_cover);
                holder.img_change.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }else {
            if (position == 0){
                holder.tv_top.setVisibility(View.INVISIBLE);
                holder.tv_bottom.setVisibility(View.VISIBLE);
            }else if (position == listData.size() - 1){
                holder.tv_top.setVisibility(View.VISIBLE);
                holder.tv_bottom.setVisibility(View.INVISIBLE);
            }else {
                holder.tv_top.setVisibility(View.VISIBLE);
                holder.tv_bottom.setVisibility(View.VISIBLE);
            }
            holder.ll_add.setVisibility(View.GONE);
            holder.ll_left.setVisibility(View.VISIBLE);
            holder.constraint_item.setVisibility(View.VISIBLE);
            holder.tv_title.setText(listData.get(position).getTitle());
            holder.tv_type.setText(listData.get(position).getType().toString());
            holder.tv_address.setText(listData.get(position).getAddress());
            GlideApp.with(context).load(listData.get(position).getImg_cover())
                    .error(R.mipmap.load_fail).into(holder.img_cover);
        }
    }

    @Override
    public int getItemCount() {
        if (isChange){
            return listData.size()+1;
        }
        return listData.size();
    }

    public RaidersListAdapter setChange(boolean change) {
        isChange = change;
        return this;
    }

    public RaidersListAdapter setOnAddListener(OnAddListener onAddListener) {
        this.onAddListener = onAddListener;
        return this;
    }

    class MyViewHolder extends ViewHolder {
        LinearLayout ll_left,ll_add;
        ConstraintLayout constraint_item;
        TextView tv_top,tv_bottom,tv_title,tv_type,tv_address;
        ImageView img_cover,img_change;

        public MyViewHolder(View itemView) {
            super(itemView);
            img_cover = itemView.findViewById(R.id.img_cover);
            img_change = itemView.findViewById(R.id.img_change);
            tv_top = itemView.findViewById(R.id.tv_top);
            tv_bottom = itemView.findViewById(R.id.tv_bottom);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_address = itemView.findViewById(R.id.tv_address);
            ll_add = itemView.findViewById(R.id.ll_add);
            ll_left = itemView.findViewById(R.id.ll_left);
            constraint_item = itemView.findViewById(R.id.constraint_item);
        }
    }
}
