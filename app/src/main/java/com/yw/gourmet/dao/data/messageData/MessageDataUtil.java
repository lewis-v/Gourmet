package com.yw.gourmet.dao.data.messageData;

import android.util.Log;

import com.yw.gourmet.Constant;
import com.yw.gourmet.adapter.ChatAdapter;
import com.yw.gourmet.dao.GreenDaoManager;
import com.yw.gourmet.dao.gen.MessageListDataDao;
import com.yw.gourmet.data.MessageListData;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

import static com.yw.gourmet.data.MessageListData.SENDING;
import static com.yw.gourmet.data.MessageListData.SEND_FAIL;

/**
 * auth: lewis-v
 * time: 2018/1/22.
 */

public class MessageDataUtil {
    public static final int LOAD_HISTORY_COUNT = 40;//一次加载历史记录的默认条数

    /**
     * 插入数据
     */
    public static int insert(MessageListData data) {
        if (data.getCli_id() == -1) {
            List<MessageListData> messageListData = GreenDaoManager.getInstance().getmDaoSession().getMessageListDataDao().queryBuilder()
                    .where(MessageListDataDao.Properties.User_id.eq(Constant.userData.getUser_id())
                            , MessageListDataDao.Properties.Get_id.in(data.getPut_id(), data.getGet_id())
                            , MessageListDataDao.Properties.Put_id.in(data.getGet_id(), data.getPut_id()))
                    .orderDesc(MessageListDataDao.Properties.Cli_id).limit(1).build().list();
            if (messageListData != null && messageListData.size()>0){
                data.setCli_id(messageListData.get(0).getCli_id()+1);
            }
        }
        try {
            GreenDaoManager.getInstance().getmDaoSession().getMessageListDataDao().insert(data);
        }catch (Exception e){
            e.printStackTrace();
        }
        return data.getCli_id();
    }

    /**
     * 更新数据
     *
     * @param data
     */
    public static void updata(MessageListData data) {
        GreenDaoManager.getInstance().getmDaoSession().getMessageListDataDao().update(data);
    }

    /**
     * 查询全部
     */
    public static List<MessageListData> querydataBy() {
        Query<MessageListData> nQuery = GreenDaoManager.getInstance().getmDaoSession().getMessageListDataDao().queryBuilder()
                .orderAsc(MessageListDataDao.Properties.Cli_id)
                .where(MessageListDataDao.Properties.User_id.eq(Constant.userData.getUser_id()))
//                .where(UserDao.Properties.Name.eq("user1"))//.where(UserDao.Properties.Id.notEq(999))
//                .orderAsc(CityInfoDao.Properties.CityName)//.limit(5)//orderDesc
                .build();
        List<MessageListData> MessageListData = nQuery.list();
        Log.e("---dao---", MessageListData.toString());
        return MessageListData;
    }

    /**
     * 清除全部
     */
    public static void clearAll() {
        GreenDaoManager.getInstance().getmDaoSession().getMessageListDataDao().deleteAll();
    }

    /**
     * 删除指定id的数据
     *
     * @param _id
     */
    public static void delete(Long _id) {
        GreenDaoManager.getInstance().getmDaoSession().getMessageListDataDao().deleteByKey(_id);
    }

    /**
     * 按条件筛选
     *
     * @param cond
     * @param condMore
     * @return
     */
    public static List<MessageListData> querydataById(WhereCondition cond, WhereCondition... condMore) {
        Query<MessageListData> nQuery = GreenDaoManager.getInstance().getmDaoSession().getMessageListDataDao().queryBuilder()
                .orderAsc(MessageListDataDao.Properties.Cli_id)
                .where(MessageListDataDao.Properties.User_id.eq(Constant.userData.getUser_id()))
                .where(cond, condMore)//.where(UserDao.Properties.Id.notEq(999))
//                .orderAsc(CityInfoDao.Properties.CityName)//.limit(5)//orderDesc
                .build();
        List<MessageListData> MessageListData = nQuery.list();
        Log.e("---dao---", MessageListData.toString());
        return MessageListData;
    }

    /**
     * 获取历史聊天记录
     *
     * @param put_id  发送者id
     * @param get_id  接收者id
     * @param startId 记录开始的客户端id,0为从最后开始
     * @return
     */
    public static List<MessageListData> getHistory(String put_id, String get_id, int startId) {
        Query<MessageListData> nQuery = GreenDaoManager.getInstance().getmDaoSession().getMessageListDataDao().queryBuilder()
                .orderDesc(MessageListDataDao.Properties.Cli_id)
                .where(MessageListDataDao.Properties.User_id.eq(Constant.userData.getUser_id())
                        , MessageListDataDao.Properties.Get_id.in(put_id, get_id)
                        , MessageListDataDao.Properties.Put_id.in(put_id, get_id)
                        , startId > 0 ? MessageListDataDao.Properties.Cli_id.lt(startId) :
                                MessageListDataDao.Properties.Cli_id.ge(0))//为0是相当于没有这个限制
                .limit(LOAD_HISTORY_COUNT)
                .build();
        List<MessageListData> MessageListData = nQuery.list();
        Log.e("---dao---", MessageListData.toString());
        return MessageListData;
    }

    /**
     * 登录时将正在发送的消息设置为发送失败
     *
     * @return
     */
    public static List<MessageListData> setSendingToFail() {
        List<MessageListData> messageListData = null;
        if (Constant.userData != null) {
            Query<MessageListData> nQuery = GreenDaoManager.getInstance().getmDaoSession().getMessageListDataDao().queryBuilder()
                    .where(MessageListDataDao.Properties.User_id.eq(Constant.userData.getUser_id())
                            , MessageListDataDao.Properties.SendStatus.eq(SENDING))
                    .build();
            messageListData = nQuery.list();
            if (messageListData != null) {
                for (MessageListData data : messageListData) {
                    data.setSendStatus(SEND_FAIL);
                    updata(data);
                }
            }
        }
        return messageListData;
    }
}
