package com.yw.gourmet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.dao.data.SaveData;

import java.util.List;

/**
 * auth: lewis-v
 * time: 2018/1/23.
 */

public class DraftAdapter extends RecyclerView.Adapter<DraftAdapter.MyViewHolder>{
    private Context context;
    private List<SaveData> data;

    public DraftAdapter(Context context, List<SaveData> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_draft,parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        switch (data.get(position).getType()){
            case Constant.TypeFlag.SHARE:
                holder.tv_type.setText("分享");
                break;
            case Constant.TypeFlag.DIARY:
                holder.tv_type.setText("日记");
                break;
            case Constant.TypeFlag.MENU:
                holder.tv_type.setText("食谱");
                break;
            case Constant.TypeFlag.RAIDERS:
                holder.tv_type.setText("攻略");
                break;
        }
        holder.tv_time.setText(data.get(position).getTime());
        holder.tv_title.setText(data.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_type,tv_time,tv_title;
        LinearLayout ll_other;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_title = itemView.findViewById(R.id.tv_title);
            ll_other = itemView.findViewById(R.id.ll_other);
        }
    }
}
