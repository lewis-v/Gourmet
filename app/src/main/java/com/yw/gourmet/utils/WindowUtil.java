package com.yw.gourmet.utils;

/**
 * Created by LYW on 2017/10/29.
 */
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class WindowUtil {
    public static int width , height;//屏幕宽度和高度

    public static void initWindowUtil(WindowManager manager){
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        width = outMetrics.widthPixels;
        height = outMetrics.heightPixels;
    }

    public int getWindowWidth(){
        return width;
    }

    public int getWindowHeight(){
        return height;
    }


}

