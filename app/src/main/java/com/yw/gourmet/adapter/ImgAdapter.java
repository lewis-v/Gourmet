package com.yw.gourmet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yw.gourmet.R;
import com.yw.gourmet.listener.OnItemClickListener;

import java.util.List;

/**
 * Created by LYW on 2017/11/22.
 */

public class ImgAdapter extends RecyclerView.Adapter<ImgAdapter.MyViewHolder>{
    private List<String> listImg;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public ImgAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public ImgAdapter(Context context, List<String> listImg){
        this.context = context;
        this.listImg = listImg;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_img,parent
                ,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Glide.with(context).load(listImg.get(position)).into(holder.img);
        if (onItemClickListener != null){
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.OnClick(v,holder.getLayoutPosition());
                }
            });
            holder.img.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return onItemClickListener.OnLongClick(v,holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listImg.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        public MyViewHolder(View itemView) {
            super(itemView);
            img = (ImageView)itemView.findViewById(R.id.img);
        }
    }
}
