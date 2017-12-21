package com.yw.gourmet.utils;

import android.util.Log;

import java.io.File;

/**
 * Created by Lewis-v on 2017/12/21.
 */

public class FileUtils {
    /**
     * 删除文件或文件目录下的所有文件
     * @param path
     * @return
     */
    public static long deleteFile(File file){
        long clearSize = 0;
        if (file.exists()) {//文件存在才操作
            if (file.isFile()) {//路径为一个文件,删除文件
                clearSize += file.length();
                file.delete();
            } else if (file.isDirectory()) {//路径为目录,删除路径下的文件
                for (File newFile :file.listFiles()){
                    clearSize += deleteFile(newFile);
                }
                file.delete();
            }
        }
        return clearSize;
    }
}
