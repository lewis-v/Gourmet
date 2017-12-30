package com.yw.gourmet.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.yw.gourmet.R;
import com.yw.gourmet.listener.OnAddListener;
import com.yw.gourmet.listener.OnDeleteListener;
import com.yw.gourmet.listener.OnItemClickListener;
import com.yw.gourmet.utils.ToastUtils;
import com.yw.gourmet.utils.UriToFileUtil;

import java.util.List;

/**
 * Created by LYW on 2017/12/6.
 */

public class ImgAddAdapter extends RecyclerView.Adapter<ImgAddAdapter.MyViewHolder>{
    private List<String> imgs;
    private Context context;
    private int MaxSize = Integer.MAX_VALUE;//最多显示数量,默认无限大
    private OnDeleteListener onDeleteListener;
    private OnItemClickListener onItemClickListener;
    private OnAddListener onAddListener;
    private String addPath ;
    private boolean isChange = true;//是否可以修改,默认可以

    public ImgAddAdapter(List<String> imgs, Context context) {
        this.imgs = imgs;
        this.context = context;
        addPath = "android.resource://"+context.getPackageName()+"/"+R.drawable.add;
    }

    public ImgAddAdapter(List<String> imgs, Context context, int maxSize, boolean isChange) {
        this(imgs, context);
        MaxSize = maxSize;
        this.isChange = isChange;
    }

    public ImgAddAdapter(List<String> imgs, Context context, int maxSize) {
        this(imgs, context);
        MaxSize = maxSize;
    }

    public ImgAddAdapter setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
        return this;
    }

    public ImgAddAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public ImgAddAdapter setOnAddListener(OnAddListener onAddListener) {
        this.onAddListener = onAddListener;
        return this;
    }

    public String getAddPath() {
        return addPath;
    }

    public ImgAddAdapter setAddPath(String addPath) {
        this.addPath = addPath;
        return this;
    }

    public boolean isChange() {
        return isChange;
    }

    public ImgAddAdapter setChange(boolean change) {
        isChange = change;
        return this;
    }

    /**
     * 添加照片
     * @param img
     */
    public synchronized void addImg(String img){
        if (imgs.size() < MaxSize) {
            imgs.add(img);
            notifyItemInserted(imgs.size() - 1);
            if (imgs.size() == MaxSize){
                notifyDataSetChanged();
            }
        }else if (imgs.size() == MaxSize){
            ToastUtils.showSingleToast("添加数量最多为"+MaxSize+"张");
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_img_add,parent,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (imgs.size() <= position && isChange){
            if (position == MaxSize){
                holder.img_delete.setVisibility(View.GONE);
                holder.img.setVisibility(View.GONE);
                holder.rl_item.setVerticalGravity(View.GONE);
            }else {
                holder.img_delete.setVisibility(View.GONE);
                holder.img.setVisibility(View.VISIBLE);
                Glide.with(context).load(Uri.parse(addPath)).into(holder.img);
                if (onAddListener != null){
                    holder.img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onAddListener.OnAdd(v,holder.getLayoutPosition());
                        }
                    });
                }
            }
        }else {
            if (isChange) {
                holder.img_delete.setVisibility(View.VISIBLE);
            }else {
                holder.img_delete.setVisibility(View.GONE);
            }

            holder.img.setVisibility(View.VISIBLE);
            Glide.with(context).load(imgs.get(position)).into(holder.img);
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
        if (onDeleteListener != null){
            holder.img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteListener.OnDelete(v,holder.getLayoutPosition());
                    imgs.remove(holder.getLayoutPosition());
                    notifyItemRemoved(holder.getLayoutPosition());
                    if (imgs.size() == MaxSize - 1){
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (isChange) {
            return imgs.size() + 1;
        }else {
            return imgs.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView img,img_delete;
        RelativeLayout rl_item;
        public MyViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            img_delete = itemView.findViewById(R.id.img_delete);
            rl_item = itemView.findViewById(R.id.rl_item);
        }
    }
}
