package com.yw.gourmet.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * auth: lewis-v
 * time: 2018/2/3.
 */

public class SoftInputUtils {
    /**
     * 隐藏对应控件的软键盘
     * @param view
     */
    public static void hideSoftInput(View view){
        InputMethodManager imm = (InputMethodManager) view
                .getContext().getApplicationContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }catch (Exception e){}
    }
}
