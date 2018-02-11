package com.yw.gourmet.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * auth: lewis-v
 * time: 2018/2/11.
 */

public class YWRecyclerView extends RecyclerView {
    private OnScrollListener onScrollListener;

    public YWRecyclerView(Context context) {
        super(context);
    }

    public YWRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public YWRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (getAdapter().getItemCount() > 0) {
            LayoutManager layoutManager = getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                if (0 == ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition()) {//滑到第一个
                    if (onScrollListener != null) {
                        onScrollListener.onLoadFirst();
                    }
                } else if (((LinearLayoutManager) layoutManager).findLastVisibleItemPosition()
                        == getAdapter().getItemCount() - 1) {//滑到最后一个
                    if (onScrollListener != null) {
                        onScrollListener.onLoadLast();
                    }
                }
            }
        }
    }

    public YWRecyclerView setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
        return this;
    }

    public interface OnScrollListener{
        void onLoadFirst();
        void onLoadLast();
    }
}
