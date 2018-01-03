package com.yw.gourmet.ui.wxapi;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Created by Lewis-v on 2018/1/3.
 */

public class WXUtils {
    /**
     * 微信位图转换
     * @param bmp
     * @param needRecycle
     * @return
     */
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 微信类型标记
     * @param type 类型
     * @return
     */
    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
