package com.yw.gourmet.center;

import com.yw.gourmet.center.event.IMessageGet;
import com.yw.gourmet.data.MessageListData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * auth: lewis-v
 * time: 2018/2/7.
 */

public class MessageCenter {
    public static final String TAG = "MessageCenter";

    private List<IMessageGet> messageEvent = new ArrayList<>();
    private ExecutorService executorService;

    private static class Instance{
        private static final MessageCenter instance = new MessageCenter();
    }

    private MessageCenter() {
         executorService = Executors.newSingleThreadExecutor();
    }

    public static MessageCenter getInstance(){
        return Instance.instance;
    }

    /**
     * 添加消息处理,在末尾
     * @param iMessageGet
     * @return
     */
    public MessageCenter addMessageHandle(IMessageGet iMessageGet){
        int index = messageEvent.indexOf(iMessageGet);
        if (index > -1){//防止重复添加同一个事件处理
            messageEvent.remove(index);
        }
        messageEvent.add(iMessageGet);
        return this;
    }

    /**
     * 添加消息处理,在顶部
     * @param iMessageGet
     * @return
     */
    public MessageCenter addMessageHandleTop(IMessageGet iMessageGet){
        int index = messageEvent.indexOf(iMessageGet);
        if (index > -1){//防止重复添加同一个事件处理
            messageEvent.remove(index);
        }
        messageEvent.add(0,iMessageGet);
        return this;
    }

    /**
     * 移除消息处理
     * @return
     */
    public MessageCenter removeMessageHandle(IMessageGet iMessageGet){
        int index = messageEvent.indexOf(iMessageGet);
        if (index > -1){
            messageEvent.remove(index);
        }
        return this;
    }

    /**
     * 发送消息
     * @param message
     * @return
     */
    public MessageCenter pushMessage(final MessageListData message){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (IMessageGet iMessageGet : messageEvent){
                    if (iMessageGet.onGetMessage(message)){//返回true为处理完毕,不在向后发送
                        return;
                    }
                }
            }
        });
        return this;
    }

    /**
     * 查看是否存在对应处理
     * @param iMessageGet
     * @return
     */
    public boolean isExist(IMessageGet iMessageGet){
        if (messageEvent != null && messageEvent.indexOf(iMessageGet)>-1){
            return true;
        }
        return false;
    }
}
