package com.yw.gourmet.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * auth: lewis-v
 * time: 2018/4/23.
 */

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public BaseViewHolder(View itemView,int type) {
        super(itemView);
    }

    public abstract void onBind(T data);
}
