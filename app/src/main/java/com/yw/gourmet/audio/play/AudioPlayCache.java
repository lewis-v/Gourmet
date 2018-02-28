package com.yw.gourmet.audio.play;

import android.os.Environment;

/**
 * auth: lewis-v
 * time: 2018/2/28.
 */

public abstract class AudioPlayCache extends AudioPlayImp {
    private String CACHE_DIR = Environment.getExternalStorageDirectory()+"/data/gourmet/chat/audio";//默认缓存地址

    @Override
    protected String handlePlayPath(String audioPath) {
        if (audioPath.startsWith("http")){//检测是否缓存
            String cacheData = getCachePath(audioPath);
            if (cacheData != null){//已有缓存,返回本地缓存地址
                return cacheData;
            }else{//无缓存,进行缓存
                return downCache(audioPath);
            }
        }
        return super.handlePlayPath(audioPath);
    }

    /**
     * 获取缓存
     * @param audioPath
     * @return
     */
    abstract protected String getCachePath(String audioPath);

    /**
     * 下载缓存
     * @param audioPath
     * @return
     */
    abstract protected String downCache(String audioPath);

    public String getCACHE_DIR() {
        return CACHE_DIR;
    }

    public AudioPlayCache setCACHE_DIR(String CACHE_DIR) {
        this.CACHE_DIR = CACHE_DIR;
        return this;
    }
}
