package com.yw.gourmet.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yw.gourmet.Constant;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.ActivityStack;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxManager;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.center.MessageCenter;
import com.yw.gourmet.center.event.IMessageGet;
import com.yw.gourmet.center.event.IMessageSendEvent;
import com.yw.gourmet.dao.data.messageData.MessageDataUtil;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.MessageListData;
import com.yw.gourmet.push.PushManager;
import com.yw.gourmet.push.PushReceiver;
import com.yw.gourmet.rxbus.EventSticky;
import com.yw.gourmet.rxbus.RxBus;
import com.yw.gourmet.rxbus.RxBusSubscriber;
import com.yw.gourmet.rxbus.RxSubscriptions;
import com.yw.gourmet.ui.chat.ChatActivity;
import com.yw.gourmet.utils.SPUtils;
import com.yw.gourmet.utils.ToastUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MultipartBody;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.yw.gourmet.Constant.NORMAL_PUSH_ID;
import static com.yw.gourmet.voiceChat.VoiceChatData.ACTION;

public class MessageService extends Service {
    public static final String TAG = "MessageService";
    private NotificationManager notifyManager;
    private IMessageGet iMessageGet;
    private IMessageSendEvent iMessageSendEvent;
    private RxManager rxManager;
    private Subscription mRxSubSticky;
    private BroadcastReceiver voiceChatReceiver;
    private ExecutorService executorService;

    public MessageService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newCachedThreadPool();
        rxManager = new RxManager();
        iMessageSendEvent = new IMessageSendEvent() {
            @Override
            public boolean onSendMessageResult(final BaseData<MessageListData> message, MessageListData messageListData, int position) {
                if (message.getStatus() == -2) {
                    PushManager.getInstance().clearAllNotification(MessageService.this)
                            .setTag(MessageService.this, PushManager.NOMAL_ALIAS, PushManager.NOMAL_TAG);
                    RxBus.getDefault().postSticky(new EventSticky("out"));
                    Constant.userData = null;
                    SPUtils.setSharedStringData(getApplicationContext(), "token", "");
                    Intent i = getPackageManager().getLaunchIntentForPackage(getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    ActivityStack.getScreenManager().clearAllActivity();
                    return true;
                }
                if (message.getStatus() == 0) {
                    messageListData.setId(message.getData().getId());
                    if (message.getData().getType() == MessageListData.IMG) {
                        messageListData.setImg(message.getData().getImg());
                    }
                    messageListData.setSendStatus(MessageListData.SEND_SUCCESS);
                    MessageDataUtil.updata(messageListData);
                } else {
                    sendMessageFail(messageListData, message.getMessage());
                }
                return false;
            }

            @Override
            public boolean onSendMessageFail(MessageListData message, int position, final String msg) {
                sendMessageFail(message, msg);
                return false;
            }
        };
        MessageCenter.getInstance().addMessageHandle(iMessageSendEvent);
        voiceChatReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String apply_id, recevice_id, content, img, name;
                    content = intent.getStringExtra("content");
                    ToastUtils.showSingleToast(intent.getStringExtra("toast"));
                    System.out.println("结束:" + content);
                    if ((apply_id = intent.getStringExtra("apply_id")) != null
                            && apply_id.equals(Constant.userData.getUser_id())) {//是自己发起的就进行处理,否则不处理
                        recevice_id = intent.getStringExtra("recevice_id");
                        final MessageListData data = new MessageListData();
                        data.setImg(content);
                        data.setLength(intent.getIntExtra("time", 0));
                        data.setType(MessageListData.VOICE_CHAT);
                        data.set_id(System.nanoTime());
                        data.setUser_id(Constant.userData.getUser_id());
                        data.setPut_id(Constant.userData.getUser_id());
                        data.setGet_id(recevice_id).setIs_read(1)
                                .setCli_id(-1);
                        data.setImg_header(Constant.userData.getImg_header());
                        data.setSendStatus(MessageListData.SEND_SUCCESS);
                        data.setNickname(Constant.userData.getNickname());
                        final MultipartBody.Builder builder = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("token", Constant.userData == null ? "0" : Constant.userData.getToken())
                                .addFormDataPart("put_id", Constant.userData.getUser_id())
                                .addFormDataPart("get_id", recevice_id)
                                .addFormDataPart("type", String.valueOf(MessageListData.VOICE_CHAT))
                                .addFormDataPart("img", content)
                                .addFormDataPart("length", String.valueOf(data.getLength()));
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                int clentId = MessageDataUtil.insert(data);
                                sendMessage(builder.build().parts(), data.setCli_id(clentId), 0);

                            }
                        });
                    }
                }
            }
        };
        registerReceiver(voiceChatReceiver, new IntentFilter(ACTION));
        setRxBus();
    }

    /**
     * 消息发送失败通知
     *
     * @param messageListData
     */
    public void sendMessageFail(MessageListData messageListData, final String msg) {
        messageListData.setSendStatus(MessageListData.SEND_FAIL);
        MessageDataUtil.updata(messageListData);
        Notification notification = new NotificationCompat
                .Builder(MessageService.this, messageListData.getGet_id())
                .setSmallIcon(R.mipmap.dialog_back)
                .setContentIntent(getDefalutIntent(FLAG_UPDATE_CURRENT
                        , messageListData.getPut_id(), messageListData.getGet_id()))
                .setContentTitle("有一条消息发送失败")
                .setTicker("有一条消息发送失败")
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .build();
        notifyManager.notify(Integer.parseInt(messageListData.getGet_id()), notification);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showSingleToast(msg);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (notifyManager == null) {
            notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (iMessageGet == null || !MessageCenter.getInstance().isExist(iMessageGet)) {
            iMessageGet = new IMessageGet() {
                @Override
                public boolean onGetMessage(final MessageListData message) {
                    if (message.getType() == MessageListData.VOICE_CHAT) {//语聊申请
                        if ((message.getId() == null || message.getId().length() <= 0)) {//未进过服务器数据库处理的数据不是聊天数据
                            if (PushReceiver.isInit) {
                                handleVoiceChatApply(message);
                            }else {
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        while (!PushReceiver.isInit){
                                            Log.i(TAG, String.valueOf(PushReceiver.isInit));
                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                return;
                                            }
                                        }
                                        handleVoiceChatApply(message);
                                    }
                                });
                            }
                        } else {//有id,为语聊后的消息通知
                            executorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    message.setUser_id(Constant.userData.getUser_id())
                                            .setIs_read(0);
                                    MessageDataUtil.insert(message);
                                }
                            });
                        }
                    } else {
                        MessageDataUtil.insert(message);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                GlideApp.with(MessageService.this)
                                        .asBitmap().load(message.getImg_header())
                                        .error(R.mipmap.ic_launcher).into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                        int id;
                                        try {
                                            id = Integer.parseInt(message.getPut_id());
                                        } catch (Exception e) {
                                            id = NORMAL_PUSH_ID;
                                        }
                                        Notification notification = new NotificationCompat
                                                .Builder(MessageService.this, String.valueOf(id))
                                                .setSmallIcon(R.mipmap.dialog_back)
                                                .setLargeIcon(resource)
                                                .setDefaults(Notification.DEFAULT_ALL)
                                                .setTicker("你有一条新消息")
                                                .setContentText(message.getContent())
                                                .setContentTitle(message.getNickname())
                                                .setAutoCancel(true)
                                                .setContentIntent(getDefalutIntent(FLAG_UPDATE_CURRENT
                                                        , message.getGet_id(), message.getPut_id()))
                                                .setPriority(Notification.PRIORITY_HIGH)
                                                .build();
                                        notifyManager.notify(id, notification);
                                        Log.i(TAG, "push:" + id);
                                    }
                                });
                            }
                        });
                    }
                    return false;
                }
            };
            MessageCenter.getInstance().addMessageHandle(iMessageGet);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void handleVoiceChatApply(MessageListData message){
        Intent chatIntent = new Intent(MessageService.this, VoiceChatService.class);
        chatIntent.putExtra("name", message.getNickname());
        chatIntent.putExtra("img", message.getImg_header());
        try {
            chatIntent.putExtra("port", Integer.parseInt(message.getImg()));
        } catch (Exception e) {
            chatIntent.putExtra("port", 0);
        }
        chatIntent.putExtra("apply_id", message.getPut_id());
        chatIntent.putExtra("recevice_id", message.getGet_id());
        chatIntent.putExtra("apply", false);
        startService(chatIntent);
    }

    /**
     * 点击消息
     *
     * @param flags 设置为FLAG_UPDATE_CURRENT才可以传入新的intent值
     * @return
     */
    public PendingIntent getDefalutIntent(int flags, String putId, String getId) {
        Intent myintent = new Intent(this, ChatActivity.class);
        myintent.putExtra("put_id", putId);
        myintent.putExtra("get_id", getId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, myintent, flags);
        return pendingIntent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBind();
    }

    public class MyBind extends Binder {
        public void sendMessage(final List<MultipartBody.Part> parts, final MessageListData messageListData, final int position) {
            MessageService.this.sendMessage(parts, messageListData, position);
        }
    }

    public void sendMessage(final List<MultipartBody.Part> parts, final MessageListData messageListData, final int position) {
        rxManager.add(Api.getInstance().SendMessage(parts), new RxSubscriberCallBack<BaseData<MessageListData>>(new RxApiCallback<BaseData<MessageListData>>() {
            @Override
            public void onSuccess(BaseData<MessageListData> model) {
                Log.e(TAG, model.getData().toString());
                MessageCenter.getInstance().pushSendMessageResult(model, messageListData, position);
            }

            @Override
            public void onFailure(int code, String msg) {
                if (messageListData.getType() != MessageListData.VOICE_CHAT) {
                    MessageCenter.getInstance().pushSendMessageFail(messageListData, position, msg);
                }
            }
        }));
    }


    public void setRxBus() {
        if (mRxSubSticky != null && !mRxSubSticky.isUnsubscribed()) {
            RxSubscriptions.remove(mRxSubSticky);
        } else {
            EventSticky s = RxBus.getDefault().getStickyEvent(EventSticky.class);
            Log.i("FFF", "获取到StickyEvent--->" + s);

            mRxSubSticky = RxBus.getDefault().toObservableSticky(EventSticky.class)
                    .flatMap(new Func1<EventSticky, Observable<EventSticky>>() {
                        @Override
                        public Observable<EventSticky> call(EventSticky eventSticky) {
                            return Observable.just(eventSticky)
                                    .map(new Func1<EventSticky, EventSticky>() {
                                        @Override
                                        public EventSticky call(EventSticky eventSticky) {
                                            // 这里模拟产生 Error
                                            return eventSticky;
                                        }
                                    })
                                    .doOnError(new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            Log.e("FFF", "onError--Sticky");
                                        }
                                    })
                                    .onErrorResumeNext(Observable.<EventSticky>empty());
                        }
                    })
                    .subscribe(new RxBusSubscriber<EventSticky>() {
                        @Override
                        protected void onEvent(EventSticky eventSticky) {
                            Log.i("FFF", "onNext--Sticky-->" + eventSticky.event);
                            if (eventSticky.event.startsWith("notification:")) {
                                String[] strings = eventSticky.event.split(":");
                                if (strings.length == 2 && notifyManager != null) {
                                    try {
                                        notifyManager.cancel(Integer.parseInt(strings[1]));
                                        Log.i(TAG, "cancel:" + strings[1]);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
            RxSubscriptions.add(mRxSubSticky);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (iMessageGet != null) {
            MessageCenter.getInstance().removeMessageHandle(iMessageGet);
        }
        if (iMessageSendEvent != null) {
            MessageCenter.getInstance().removeMessageHandle(iMessageSendEvent);
        }
        if (rxManager != null) {
            rxManager.clear();
        }
        if (mRxSubSticky != null) {
            mRxSubSticky.unsubscribe();
            RxSubscriptions.remove(mRxSubSticky);
            mRxSubSticky = null;
        }
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        notifyManager.cancelAll();
    }
}
