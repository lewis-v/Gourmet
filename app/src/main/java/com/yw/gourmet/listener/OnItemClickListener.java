package com.yw.gourmet.listener;

import android.view.View;

/**
 * Created by LYW on 2017/11/18.
 */

public interface OnItemClickListener {
    void OnClick(View v, int position);
    boolean OnLongClick(View v,int position);
}
