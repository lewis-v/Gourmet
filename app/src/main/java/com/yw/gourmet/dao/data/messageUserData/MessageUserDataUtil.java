package com.yw.gourmet.dao.data.messageUserData;

import android.util.Log;

import com.yw.gourmet.dao.GreenDaoManager;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

/**
 * auth: lewis-v
 * time: 2018/3/2.
 */

public class MessageUserDataUtil {
    /**
     * 插入数据
     */
    public static void insert(MessageUserData data){
        GreenDaoManager.getInstance().getmDaoSession().getMessageUserDataDao().insert(data);
    }

    /**
     * 更新数据
     * @param data
     */
    public static void updata(MessageUserData data){
        GreenDaoManager.getInstance().getmDaoSession().getMessageUserDataDao().update(data);
    }

    /**
     * 查询全部
     */
    public static List<MessageUserData> querydataBy() {
        Query<MessageUserData> nQuery =  GreenDaoManager.getInstance().getmDaoSession()
                .getMessageUserDataDao().queryBuilder()
                .build();
        List<MessageUserData> MessageUserData = nQuery.list();
        Log.e("---dao---",MessageUserData.toString());
        return MessageUserData;
    }

    /**
     * 清除全部
     */
    public static void clearAll(){
        GreenDaoManager.getInstance().getmDaoSession().getMessageUserDataDao().deleteAll();
    }

    /**
     * 删除指定id的数据
     * @param _id
     */
    public static void delete(Long _id){
        GreenDaoManager.getInstance().getmDaoSession().getMessageUserDataDao().deleteByKey(_id);
    }

    /**
     * 按条件筛选
     * @param cond
     * @param condMore
     * @return
     */
    public static List<MessageUserData> querydataById(WhereCondition cond, WhereCondition... condMore){
        Query<MessageUserData> nQuery =  GreenDaoManager.getInstance().getmDaoSession()
                .getMessageUserDataDao().queryBuilder()
                .where(cond,condMore)
                .build();
        List<MessageUserData> MessageUserData = nQuery.list();
        Log.e("---dao---",MessageUserData.toString());
        return MessageUserData;
    }
}
