package com.yw.gourmet.dao.data.audioCacheData;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * auth: lewis-v
 * time: 2018/2/28.
 */
@Entity
public class AudioCacheData {
    @Id(autoincrement = true)
    private long _id;

    private String user_id;//存储者id
    private String netPath;//网络链接
    private String localPath;//本地存储地址
    private long saveTime;//存储的时间

    @Generated(hash = 1887075353)
    public AudioCacheData(long _id, String user_id, String netPath,
            String localPath, long saveTime) {
        this._id = _id;
        this.user_id = user_id;
        this.netPath = netPath;
        this.localPath = localPath;
        this.saveTime = saveTime;
    }

    @Generated(hash = 1547616168)
    public AudioCacheData() {
    }

    public long get_id() {
        return _id;
    }

    public AudioCacheData set_id(long _id) {
        this._id = _id;
        return this;
    }

    public String getUser_id() {
        return user_id;
    }

    public AudioCacheData setUser_id(String user_id) {
        this.user_id = user_id;
        return this;
    }

    public String getNetPath() {
        return netPath;
    }

    public AudioCacheData setNetPath(String netPath) {
        this.netPath = netPath;
        return this;
    }

    public String getLocalPath() {
        return localPath;
    }

    public AudioCacheData setLocalPath(String localPath) {
        this.localPath = localPath;
        return this;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public AudioCacheData setSaveTime(long saveTime) {
        this.saveTime = saveTime;
        return this;
    }

    @Override
    public String toString() {
        return "AudioCacheData{" +
                "_id=" + _id +
                ", user_id='" + user_id + '\'' +
                ", netPath='" + netPath + '\'' +
                ", localPath='" + localPath + '\'' +
                ", saveTime=" + saveTime +
                '}';
    }
}
