package com.yw.gourmet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yw.gourmet.R;
import com.yw.gourmet.listener.OnAddListener;
import com.yw.gourmet.listener.OnDeleteListener;
import com.yw.gourmet.listener.OnItemClickListener;

import java.util.List;

/**
 * Created by Lewis-v on 2017/12/21.
 */

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.MyViewHolder>{
    private Context context;
    private List<String> list;
    private boolean isChange = true;//是否可修改,默认可以
    private OnAddListener onAddListener;
    private OnDeleteListener onDeleteListener;
    private OnItemClickListener onItemClickListener;

    public IngredientAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    public IngredientAdapter(Context context, List<String> list, boolean isChange) {
        this.context = context;
        this.list = list;
        this.isChange = isChange;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_ingredient,parent,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (isChange){//可修改时
            if (position >= list.size()) {//比列表长度多出的那个位置,是添加按钮的位置
                holder.tv_name.setVisibility(View.GONE);
                holder.img_delete.setVisibility(View.GONE);
                holder.img_add.setVisibility(View.VISIBLE);
            } else {
                holder.tv_name.setVisibility(View.VISIBLE);
                holder.img_delete.setVisibility(View.VISIBLE);
                holder.img_add.setVisibility(View.GONE);
                String[] content = list.get(position).split("&&");
                if (content.length == 2) {
                    holder.tv_name.setText(content[0] + "\t\t" + content[1]);
                }else {
                    holder.tv_name.setText(list.get(position));
//                    list.remove(position);
//                    notifyDataSetChanged();
                }
            }
        }else {//不可修改时
            holder.tv_name.setVisibility(View.VISIBLE);
            holder.img_delete.setVisibility(View.GONE);
            holder.img_add.setVisibility(View.GONE);
            String[] content = list.get(position).split("&&");
            if (content.length == 2) {
                holder.tv_name.setText(content[0] + "\t\t" + content[1]);
            }else {
                holder.tv_name.setText(list.get(position));
//                list.remove(position);
//                notifyDataSetChanged();
            }
        }
        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChange && holder.getLayoutPosition() >= list.size()){
                    if (onAddListener != null){
                        onAddListener.OnAdd(v,holder.getLayoutPosition());
                    }
                }else {
                    if (onItemClickListener != null){
                        onItemClickListener.OnClick(v,holder.getLayoutPosition());
                    }
                }
            }
        });
        if (onItemClickListener != null){
            holder.ll_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return onItemClickListener.OnLongClick(v,holder.getLayoutPosition());
                }
            });
        }

        if (onDeleteListener != null){
            holder.img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteListener.OnDelete(v,holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (isChange) {
            return list.size()+1;
        }else {
            return list.size();
        }
    }

    public IngredientAdapter setOnAddListener(OnAddListener onAddListener) {
        this.onAddListener = onAddListener;
        return this;
    }

    public IngredientAdapter setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
        return this;
    }

    public IngredientAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public IngredientAdapter setChange(boolean change) {
        isChange = change;
        return this;
    }

    public boolean isChange() {
        return isChange;
    }

    public boolean isEmpty(){
        if (list.size() == 0){
            return true;
        }
        for (String string : list){
            if (string.trim().isEmpty()){
                return true;
            }
        }
        return false;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout ll_item;
        TextView tv_name;
        ImageView img_delete,img_add;

        public MyViewHolder(View itemView) {
            super(itemView);
            ll_item = (LinearLayout)itemView.findViewById(R.id.ll_item);
            tv_name = (TextView)itemView.findViewById(R.id.tv_name);
            img_delete = (ImageView)itemView.findViewById(R.id.img_delete);
            img_add = (ImageView)itemView.findViewById(R.id.img_add);
        }
    }
}
