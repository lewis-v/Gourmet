package com.yw.gourmet.listener;

import android.view.View;

/**
 * auth: lewis-v
 * time: 2018/1/28.
 */

public interface OnMoveListener {
    void onUp(View vIew, int position);
    void onDelete(View vIew, int position);
    void onDown(View vIew, int position);

}
