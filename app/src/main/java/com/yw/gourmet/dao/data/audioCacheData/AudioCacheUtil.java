package com.yw.gourmet.dao.data.audioCacheData;

import android.util.Log;

import com.yw.gourmet.dao.GreenDaoManager;
import com.yw.gourmet.dao.gen.AudioCacheDataDao;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

/**
 * auth: lewis-v
 * time: 2018/2/28.
 */

public class AudioCacheUtil {
    /**
     * 插入数据
     */
    public static void insert(AudioCacheData data){
        GreenDaoManager.getInstance().getmDaoSession().getAudioCacheDataDao().insert(data);
    }

    /**
     * 更新数据
     * @param data
     */
    public static void updata(AudioCacheData data){
        GreenDaoManager.getInstance().getmDaoSession().getAudioCacheDataDao().update(data);
    }

    /**
     * 查询全部
     */
    public static List<AudioCacheData> querydataBy() {
        Query<AudioCacheData> nQuery =  GreenDaoManager.getInstance().getmDaoSession().getAudioCacheDataDao().queryBuilder()
                .orderDesc(AudioCacheDataDao.Properties.SaveTime)
//                .where(UserDao.Properties.Name.eq("user1"))//.where(UserDao.Properties.Id.notEq(999))
//                .orderAsc(CityInfoDao.Properties.CityName)//.limit(5)//orderDesc
                .build();
        List<AudioCacheData> cityInfos = nQuery.list();
        Log.e("---dao---",cityInfos.toString());
        return cityInfos;
    }

    /**
     * 清除全部
     */
    public static void clearAll(){
        GreenDaoManager.getInstance().getmDaoSession().getAudioCacheDataDao().deleteAll();
    }

    /**
     * 删除指定id的数据
     * @param _id
     */
    public static void delete(Long _id){
        GreenDaoManager.getInstance().getmDaoSession().getAudioCacheDataDao().deleteByKey(_id);
    }

    /**
     * 按条件筛选
     * @param cond
     * @param condMore
     * @return
     */
    public static List<AudioCacheData> querydataById(WhereCondition cond, WhereCondition... condMore){
        Query<AudioCacheData> nQuery =  GreenDaoManager.getInstance().getmDaoSession().getAudioCacheDataDao().queryBuilder()
                .orderDesc(AudioCacheDataDao.Properties.SaveTime)
                .where(cond,condMore)//.where(UserDao.Properties.Id.notEq(999))
//                .orderAsc(CityInfoDao.Properties.CityName)//.limit(5)//orderDesc
                .build();
        List<AudioCacheData> cityInfos = nQuery.list();
        Log.e("---dao---",cityInfos.toString());
        return cityInfos;
    }
}
