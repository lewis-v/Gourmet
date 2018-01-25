package com.yw.gourmet.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.yw.gourmet.R;

import java.util.List;

/**
 * auth: lewis-v
 * time: 2018/1/24.
 */

public class ChooseListAdapter extends RecyclerView.Adapter<ChooseListAdapter.MyViewHolder>{
    private Context context;
    private List<String> list;
    private int choosePosition = 0;//选择的位置,默认为0

    public ChooseListAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_choose_list,parent,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.tv_name.setText(list.get(position));
        if (position == choosePosition){
            holder.radio.setChecked(true);
        }else {
            holder.radio.setChecked(false);
        }
        Log.e("---list",list.get(position)+";"+position);
        holder.constraint_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choosePosition != holder.getLayoutPosition()){
                    int cachePosition = choosePosition;
                    choosePosition = holder.getLayoutPosition();
                    notifyItemChanged(choosePosition);
                    notifyItemChanged(cachePosition);
                }
            }
        });
        holder.radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choosePosition != holder.getLayoutPosition()){
                    int cachePosition = choosePosition;
                    choosePosition = holder.getLayoutPosition();
                    notifyItemChanged(choosePosition);
                    notifyItemChanged(cachePosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public int getChoosePosition() {
        return choosePosition;
    }

    public ChooseListAdapter setChoosePosition(int choosePosition) {
        this.choosePosition = choosePosition;
        return this;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        RadioButton radio;
        ConstraintLayout constraint_item;

        public MyViewHolder(View itemView) {
            super(itemView);
            constraint_item = itemView.findViewById(R.id.constraint_item);
            radio = itemView.findViewById(R.id.radio);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }
}
