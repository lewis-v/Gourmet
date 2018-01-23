package com.yw.gourmet.dao.data;

import android.util.Log;

import com.yw.gourmet.App;
import com.yw.gourmet.Constant;
import com.yw.gourmet.dao.GreenDaoManager;
import com.yw.gourmet.dao.gen.SaveDataDao;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

/**
 * auth: lewis-v
 * time: 2018/1/22.
 */

public class SaveDataUtil {
    /**
     * 插入数据
     */
    public static void insert(SaveData data){
        GreenDaoManager.getInstance().getmDaoSession().getSaveDataDao().insert(data);
    }

    /**
     * 更新数据
     * @param data
     */
    public static void updata(SaveData data){
        GreenDaoManager.getInstance().getmDaoSession().getSaveDataDao().update(data);
    }

    /**
     * 查询全部
     */
    public static List<SaveData> querydataBy() {
        Query<SaveData> nQuery =  GreenDaoManager.getInstance().getmDaoSession().getSaveDataDao().queryBuilder()
                .orderDesc(SaveDataDao.Properties.Change_time)
                .where(SaveDataDao.Properties.User_id.eq(Constant.userData.getId()))
//                .where(UserDao.Properties.Name.eq("user1"))//.where(UserDao.Properties.Id.notEq(999))
//                .orderAsc(CityInfoDao.Properties.CityName)//.limit(5)//orderDesc
                .build();
        List<SaveData> cityInfos = nQuery.list();
        Log.e("---dao---",cityInfos.toString());
        return cityInfos;
    }

    /**
     * 清除全部
     */
    public static void clearAll(){
        GreenDaoManager.getInstance().getmDaoSession().getSaveDataDao().deleteAll();
    }

    public static void delete(Long _id){
        GreenDaoManager.getInstance().getmDaoSession().getSaveDataDao().deleteByKey(_id);
    }

    /**
     * 按条件筛选
     * @param cond
     * @param condMore
     * @return
     */
    public static List<SaveData> querydataById(WhereCondition cond, WhereCondition... condMore){
        Query<SaveData> nQuery =  GreenDaoManager.getInstance().getmDaoSession().getSaveDataDao().queryBuilder()
                .orderDesc(SaveDataDao.Properties.Change_time)
                .where(cond,condMore)//.where(UserDao.Properties.Id.notEq(999))
//                .orderAsc(CityInfoDao.Properties.CityName)//.limit(5)//orderDesc
                .build();
        List<SaveData> cityInfos = nQuery.list();
        Log.e("---dao---",cityInfos.toString());
        return cityInfos;
    }
}
