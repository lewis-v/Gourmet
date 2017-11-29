package com.yw.gourmet.listener;

import android.view.View;

/**
 * Created by LYW on 2017/11/29.
 */

public interface OnReMarkListener {
    void OnGoodClick(View view, int position);
    void OnBadClick(View view,int position);
    void OnCommentClick(View view,int position);
}
