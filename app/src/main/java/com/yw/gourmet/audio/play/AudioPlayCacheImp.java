package com.yw.gourmet.audio.play;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.yw.gourmet.Constant;
import com.yw.gourmet.dao.data.audioCacheData.AudioCacheData;
import com.yw.gourmet.dao.data.audioCacheData.AudioCacheUtil;
import com.yw.gourmet.dao.gen.AudioCacheDataDao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * auth: lewis-v
 * time: 2018/2/28.
 */

public class AudioPlayCacheImp extends AudioPlayCache {
    private String CACHE_DIR = Environment.getExternalStorageDirectory()+"/data/gourmet/chat/audio";//默认缓存地址

    /**
     * 获取缓存
     * @param audioPath
     * @return
     */
    protected String getCachePath(String audioPath){
        List<AudioCacheData> list =AudioCacheUtil.querydataById(AudioCacheDataDao.Properties.NetPath.eq(audioPath));
        if (list != null && list.size()>0) {
            return list.get(0).getLocalPath();
        }else {
            return null;
        }
    }

    /**
     * 下载缓存,返回缓存后的地址
     * @param audioPath
     * @return
     */
    protected String downCache(String audioPath){
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(audioPath)
                .build();
        Response response = null;
        InputStream is = null;
        FileOutputStream fos = null; // 储存下载文件的目录
        File file = null;
        try {
            response = httpClient.newCall(request).execute();
            byte[] buf = new byte[2048];
            int len = 0;
            String savePath = isExistDir(CACHE_DIR, getNameFromUrl(audioPath));
            is = response.body().byteStream();
            file = new File(savePath);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush(); // 下载完成
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) { }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) { }
        }
        if (file == null){
            return null;
        }
        saveCache(audioPath,file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException
     * 判断下载目录是否存在
     */
    private String isExistDir(String saveDir,String fileName) throws IOException { // 下载位置
        File downloadFile = new File(saveDir);
        if (!downloadFile.exists()){
            downloadFile.mkdirs();
        }
        downloadFile = new File(saveDir,fileName);
        if (!downloadFile.exists()){
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * @param url
     * @return
     * 从下载连接中解析出文件名
     */
    @NonNull
    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * 将缓存的对应地址存储在数据库中
     * @param netPath
     * @param localCachePath
     */
    protected void saveCache(String netPath,String localCachePath){
        AudioCacheUtil.insert(new AudioCacheData()
                .set_id(System.currentTimeMillis())
                .setLocalPath(localCachePath)
                .setNetPath(netPath)
                .setSaveTime(System.currentTimeMillis()));
    }

    public String getCACHE_DIR() {
        return CACHE_DIR;
    }

    public AudioPlayCacheImp setCACHE_DIR(String CACHE_DIR) {
        this.CACHE_DIR = CACHE_DIR;
        return this;
    }
}
