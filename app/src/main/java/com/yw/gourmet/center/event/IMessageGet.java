package com.yw.gourmet.center.event;

import com.yw.gourmet.data.MessageListData;

/**
 * auth: lewis-v
 * time: 2018/2/7.
 */

public interface IMessageGet extends IEvent{
    boolean onGetMessage(MessageListData message);
}
