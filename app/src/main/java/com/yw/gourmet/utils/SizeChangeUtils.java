package com.yw.gourmet.utils;

import java.text.DecimalFormat;

/**
 * Created by Lewis-v on 2017/12/21.
 */

public class SizeChangeUtils {
    /**
     * 将byte转换成适应期数值大小的最大单位
     * @param size
     * @return
     */
    public static String getSizeByBytes(double size) {
        DecimalFormat decimalFormat = new DecimalFormat(".##");
        double sizeCache;
        if (size < 1024) {//B
            return size + "B";
        } else if ((sizeCache = size / 1024) < 1024) {//KB
            return decimalFormat.format(sizeCache) + "KB";
        } else if ((sizeCache /= 1024) < 1024) {//MB
            return decimalFormat.format(sizeCache) + "MB";
        } else if ((sizeCache /= 1024) < 1024) {//GB
            return decimalFormat.format(sizeCache) + "GB";
        } else {//TB
            return decimalFormat.format(sizeCache /= 1024) + "TB";
        }
    }

    /**
     * 将KB转换成适应期数值大小的最大单位
     * @param size
     * @return
     */
    public static String getSizeByKB(double size){
        return getSizeByBytes(size*1024);
    }

    /**
     * 将MB转换成适应期数值大小的最大单位
     * @param size
     * @return
     */
    public static String getSizeByMB(double size){
        return getSizeByKB(size*1024);
    }

    /**
     * 将GB转换成适应期数值大小的最大单位
     * @param size
     * @return
     */
    public static String getSizeByGB(double size){
        return getSizeByMB(size*1024);
    }

    /**
     * 将TB转换成适应期数值大小的最大单位
     * @param size
     * @return
     */
    public static String getSizeByTB(double size){
        return getSizeByGB(size*1024);
    }
}
