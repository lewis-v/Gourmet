package com.yw.gourmet.center.event;

import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.MessageListData;

/**
 * auth: lewis-v
 * time: 2018/2/12.
 */

public interface IMessageSendEvent extends IEvent {
    boolean onSendMessageResult(BaseData message,MessageListData MessageListData, int position);
    boolean onSendMessageFail(MessageListData message,int position,String msg);
}
