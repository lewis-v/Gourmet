package com.yw.gourmet.ui.chat;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.ChatAdapter;
import com.yw.gourmet.audio.play.AudioPlayListener;
import com.yw.gourmet.audio.play.AudioPlayManager;
import com.yw.gourmet.audio.play.AudioPlayMode;
import com.yw.gourmet.audio.recoder.AudioRecoderData;
import com.yw.gourmet.audio.recoder.AudioRecoderListener;
import com.yw.gourmet.audio.recoder.AudioRecoderManager;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.center.MessageCenter;
import com.yw.gourmet.center.event.IMessageGet;
import com.yw.gourmet.dao.data.messageData.MessageDataUtil;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.MessageListData;
import com.yw.gourmet.data.UserData;
import com.yw.gourmet.dialog.MyDialogPhotoChooseFragment;
import com.yw.gourmet.listener.OnRefreshListener;
import com.yw.gourmet.rxbus.EventSticky;
import com.yw.gourmet.rxbus.RxBus;
import com.yw.gourmet.service.VoiceChatService;
import com.yw.gourmet.ui.imgShow.ImgShowActivity;
import com.yw.gourmet.utils.SPUtils;
import com.yw.gourmet.utils.ShareTransitionUtil;
import com.yw.gourmet.utils.SoftInputUtils;
import com.yw.gourmet.utils.StringHandleUtils;
import com.yw.gourmet.utils.ToastUtils;
import com.yw.gourmet.utils.WindowUtil;
import com.yw.gourmet.widget.YWRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.yw.gourmet.utils.SoftInputUtils.hideSoftInput;

public class ChatActivity extends BaseActivity<ChatPresenter> implements ChatContract.View
        , View.OnClickListener {
    private final static String TAG = "ChatActivity";
    private final static int TEXT = 0;//发送文本模式
    private final static int VOICE = 1;//发送语音模式

    private ImageView img_back, img_type, img_add, img_emoticon, img_voice;
    private YWRecyclerView recycler_chat;
    private EditText et_chat;
    private TextView tv_voice, tv_tool, tv_send, tv_voice_tip;
    private int sendMode = 0;//发送的模式,默认为文本
    private ChatAdapter adapter;
    private List<MessageListData> listData = new ArrayList<>();
    private String get_id, put_id;//接收者id与发送者id
    private IMessageGet iMessageGet;
    private LinearLayout ll_take_photo, ll_img, ll_add_other, ll_bottom, ll_voice_chat;
    private FrameLayout fl_chat;
    private RelativeLayout rl_voice;
    private boolean isLoadHistory = false;//是否在加载历史中
    private boolean isCancelVoice = false;//是否取消语音
    private AudioPlayMode audioPlayMode = AudioPlayMode.MEGAPHONE;//语音播放模式,默认扩音器
    private boolean isResume = false;//是否前台
    private int chatHeight = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initView() {
        view_parent = findViewById(R.id.view_parent);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        fl_chat = findViewById(R.id.fl_chat);
        fl_chat.post(new Runnable() {
            @Override
            public void run() {
                chatHeight = fl_chat.getHeight();
            }
        });

        ll_add_other = findViewById(R.id.ll_add_other);
        ll_img = findViewById(R.id.ll_img);
        ll_take_photo = findViewById(R.id.ll_take_photo);
        ll_bottom = findViewById(R.id.ll_bottom);
        ll_voice_chat = findViewById(R.id.ll_voice_chat);

        ll_take_photo.setOnClickListener(this);
        ll_img.setOnClickListener(this);
        ll_voice_chat.setOnClickListener(this);

        img_back = (ImageView) findViewById(R.id.img_back);
        img_type = (ImageView) findViewById(R.id.img_type);
        img_add = (ImageView) findViewById(R.id.img_add);
        img_emoticon = (ImageView) findViewById(R.id.img_emoticon);
        img_voice = findViewById(R.id.img_voice);

        img_emoticon.setOnClickListener(this);
        img_add.setOnClickListener(this);
        img_back.setOnClickListener(this);
        img_type.setOnClickListener(this);

        recycler_chat = findViewById(R.id.recycler_chat);
        recycler_chat.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager manager = new LinearLayoutManager(this);
//        manager.setStackFromEnd(true);
        recycler_chat.setLayoutManager(manager);
        adapter = new ChatAdapter(this, listData);
        recycler_chat.setAdapter(adapter);
        adapter.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void OnRefresh(View view, int position) {
                reSendMessage(position);
            }
        });
        adapter.setOnVoiceClickListener(new ChatAdapter.OnVoiceClickListener() {
            @Override
            public void onVoiceClick(View view, int position) {
                AudioPlayManager.getInstance().play(listData.get(position).getImg(), ChatActivity.this, audioPlayMode);
            }
        });
        adapter.setOnImgClickListener(new ChatAdapter.OnImgClickListener() {
            @Override
            public void onClick(View view, int position) {
                final String shareFlag = "tran" + (int) (Math.random() * 1000);
                Intent intent = new Intent(ChatActivity.this, ImgShowActivity.class);
                ArrayList<String> list = new ArrayList<String>();
                list.add(listData.get(position).getImg());
                intent.putStringArrayListExtra("img", list);
                intent.putExtra("position", 0);
                intent.putExtra("shareFlag", shareFlag);

                if (android.os.Build.VERSION.SDK_INT > 20) {
                    setExitSharedElementCallback(new SharedElementCallback() {
                        @Override
                        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                            super.onMapSharedElements(names, sharedElements);
                        }
                    });
                    ShareTransitionUtil.position = -1;
                    view.setTransitionName(shareFlag);
                    startActivity(intent
                            , ActivityOptions.makeSceneTransitionAnimation(ChatActivity.this
                                    , view, shareFlag).toBundle());
                } else {
                    startActivity(intent);
                }
//                new MyDialogPhotoShowFragment().addImgString(listData.get(position).getImg()).show(getSupportFragmentManager(),"img");
            }
        });
        recycler_chat.setOnScrollListener(new YWRecyclerView.OnScrollListener() {
            @Override
            public void onLoadFirst() {
                if (!isLoadHistory && listData.size() > 0 && listData.get(0).getCli_id() > 0) {
                    getHistory(put_id, get_id, listData.get(0).getCli_id());
                }
            }

            @Override
            public void onLoadLast() {

            }
        });
        recycler_chat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideSoftInput(et_chat);
                    if (ll_add_other.getVisibility() == View.VISIBLE) {
                        ll_add_other.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });

        et_chat = (EditText) findViewById(R.id.et_chat);
        et_chat.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {//监听软键盘发送
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendTextMessage();
                    return true;
                }
                return false;
            }
        });
        et_chat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {//监听焦点变化
                if (hasFocus) {
                    if (ll_add_other.getVisibility() == View.VISIBLE) {
                        setAddViewShow(false);
                    }
                    if (listData.size() > 0) {
                       moveListBottom();
                    }
                }
            }
        });
        //监听输入的字符变化
        et_chat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_chat.getText().toString().trim().length() > 0) {
                    if (tv_send.getVisibility() != View.VISIBLE) {
                        tv_send.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (tv_send.getVisibility() == View.VISIBLE) {
                        tv_send.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tv_voice = (TextView) findViewById(R.id.tv_voice);
        tv_tool = (TextView) findViewById(R.id.tv_tool);
        tv_send = findViewById(R.id.tv_send);
        tv_voice_tip = findViewById(R.id.tv_voice_tip);

        tv_voice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return VoiceListener(event);
            }
        });
        tv_tool.setText(getIntent().getStringExtra("nickname"));
        tv_send.setOnClickListener(this);

        rl_voice = findViewById(R.id.rl_voice);

        put_id = getIntent().getStringExtra("put_id");
        get_id = getIntent().getStringExtra("get_id");
        if (get_id.equals(Constant.userData.getUser_id())) {//确保发送者一定是自己
            String cache = get_id;
            get_id = put_id;
            put_id = cache;
        }
        mPresenter.getUserInfo(new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("token", Constant.userData == null ? "0" : Constant.userData.getToken())
                .addFormDataPart("id", get_id).build().parts());
        getHistory(put_id, get_id, 0);

        iMessageGet = new IMessageGet() {
            @Override
            public boolean onGetMessage(final MessageListData message) {
                if (message.getGet_id().equals(Constant.userData.getUser_id())) {
                    if (message.getId() != null && message.getId().length() > 0) {//没有id的说明未经过服务器数据库处理的,这些统一由后台服务处理
                        if (message.getType() == MessageListData.VOICE_CHAT) {//语聊结束后的信息
                            message.setUser_id(Constant.userData.getUser_id())
                                    .setIs_read(0)
                                    .setCli_id(listData.size() == 0 ? 0 : listData.get(listData.size() - 1).getCli_id() + 1);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    message.setCli_id(listData.size() == 0 ? 0
                                            : listData.get(listData.size() - 1).getCli_id() + 1);
                                    listData.add(message);
                                    adapter.notifyItemInserted(listData.size() - 1);
                                    moveListBottom();
                                }
                            });
                            mPresenter.insertDB(message);
                        } else {
                            if (isResume) {
                                message.setUser_id(Constant.userData.getUser_id())
                                        .setIs_read(0)
                                        .setCli_id(listData.size() == 0 ? 0 : listData.get(listData.size() - 1).getCli_id() + 1);
                                mPresenter.insertDB(message);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listData.add(message);
                                        adapter.notifyItemInserted(listData.size() - 1);
                                        moveListBottom();
                                        MultipartBody.Builder builder1 = new MultipartBody.Builder()
                                                .setType(MultipartBody.FORM)
                                                .addFormDataPart("token", Constant.userData == null ? "0" : Constant.userData.getToken())
                                                .addFormDataPart("id", message.getId());
                                        mPresenter.setMessageRead(builder1.build().parts());
                                    }
                                });
                                return true;
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listData.add(message);
                                        adapter.notifyItemInserted(listData.size() - 1);
                                        moveListBottom();
                                    }
                                });
                            }
                        }
                    }
                }
                return false;
            }
        };
        MessageCenter.getInstance().addMessageHandleTop(iMessageGet);
        //初始化录音
        AudioRecoderManager.getInstance().setAudioRecoderListener(new AudioRecoderListener() {
            @Override
            public void onStart() {
                rl_voice.setVisibility(View.VISIBLE);
                tv_voice_tip.setText("手指上滑,取消发送");
                SoftInputUtils.hideSoftInput(et_chat);
            }

            @Override
            public void onStop(AudioRecoderData audioRecoderData) {
                tv_voice.setText(R.string.touch_say);
                rl_voice.setVisibility(View.GONE);
                sendVoiceMessage(audioRecoderData);
            }

            @Override
            public void onFail(Exception e, String msg) {
                e.printStackTrace();
                tv_voice.setText(R.string.touch_say);
                rl_voice.setVisibility(View.GONE);
                if (msg.equals("无录音/读写权限")) {
                    getPermission();
                }
            }

            @Override
            public void onCancel() {
                tv_voice.setText(R.string.touch_say);
                rl_voice.setVisibility(View.GONE);
            }

            @Override
            public void onSoundSize(int level) {
                if (level < 10) {
                    img_voice.setImageResource(R.drawable.voice_0);
                } else if (level < 20) {
                    img_voice.setImageResource(R.drawable.voice_1);
                } else if (level < 30) {
                    img_voice.setImageResource(R.drawable.voice_2);
                } else if (level < 40) {
                    img_voice.setImageResource(R.drawable.voice_3);
                } else {
                    img_voice.setImageResource(R.drawable.voice_4);
                }
            }
        });
        //初始化音频播放
        AudioPlayManager.getInstance().setPlayListener(new AudioPlayListener() {
            @Override
            public void onPlay(String audioPath) {
                adapter.voicePlay(audioPath);
            }

            @Override
            public void onProgress(int progress, int maxSize) {

            }

            @Override
            public void onPause() {
                Log.e(TAG, "pause");
                adapter.voiceStop();
            }

            @Override
            public void onStop() {
                Log.e(TAG, "destroy");
                adapter.voiceStop();
            }

            @Override
            public void onFail(Exception e, String msg) {
                adapter.voiceStop();
                e.printStackTrace();
                if (msg.equals("无读取权限")) {
                    getPermission();
                }
            }
        });
        audioPlayMode = AudioPlayMode.getByTypeName(SPUtils.getSharedIntData(getApplicationContext(), "audio_play_mode"));
    }

    public void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.RECORD_AUDIO}, 0);
        } else {
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.RECORD_AUDIO}, 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RxBus.getDefault().postSticky(new EventSticky("notification:" + get_id));
        mPresenter.onResume();
        setAllMessageRead();
        isResume = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
        isResume = false;
    }

    /**
     * 加载历史记录
     *
     * @param put_id
     * @param get_id
     * @param startId
     */
    public void getHistory(String put_id, String get_id, int startId) {
        isLoadHistory = true;
        mPresenter.getHistory(put_id, get_id, startId);
    }

    /**
     * 发送语音的触控事件
     *
     * @param event
     * @return
     */
    public boolean VoiceListener(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isCancelVoice = false;
                AudioRecoderManager.getInstance().start(getApplicationContext());
                return true;
            case MotionEvent.ACTION_MOVE:
                if (WindowUtil.height - event.getRawY() > ll_bottom.getHeight()) {
                    tv_voice_tip.setText("松开手指,取消发送");
                    tv_voice.setText("松开手指,取消发送");
                    isCancelVoice = true;
                } else {
                    tv_voice.setText("松开 结束");
                    tv_voice_tip.setText("手指上滑,取消发送");
                    isCancelVoice = false;
                }
                return false;
            case MotionEvent.ACTION_UP:
                if (isCancelVoice) {
                    AudioRecoderManager.getInstance().cancel(getApplicationContext());
                } else {
                    AudioRecoderManager.getInstance().stop(getApplicationContext());
                }
                return false;
        }
        return false;
    }

    @Override
    public void onSendSuccess(MessageListData messageListData, int position) {
        Log.i(TAG,"success:"+position+";size:"+listData.size());
        for (MessageListData data : listData){
            Log.i(TAG,"success:"+data.toString());

        }
        if (messageListData.getType() == MessageListData.VOICE_CHAT) {
            listData.add(messageListData);
            adapter.notifyItemInserted(listData.size() - 1);
            moveListBottom();
        } else {
            Log.i(TAG,"success:"+messageListData.get_id()+":"+listData.get(position).get_id());
            if (!messageListData.get_id().equals(listData.get(position).get_id())) {//位置变了,需要重新获取位置
                position = adapter.getPositionBy_id(messageListData.get_id());
            }
            Log.i(TAG,"success:"+position);
            listData.get(position).setSendStatus(MessageListData.SEND_SUCCESS)
                    .setId(messageListData.getId());
            adapter.notifyItemChanged(position);
        }
        if (isResume) {
            mPresenter.updataDB(listData.get(position));
        }
    }

    /**
     * 将聊天列表移动到底部
     */
    public void moveListBottom(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                if (recycler_chat.getAllHeihgt() >= (chatHeight == 0?fl_chat.getHeight():chatHeight )){
//                    ((LinearLayoutManager)recycler_chat.getLayoutManager()).setStackFromEnd(true);
//                }else {
//                    ((LinearLayoutManager)recycler_chat.getLayoutManager()).setStackFromEnd(false);
//                }
                recycler_chat.smoothScrollToPosition(listData.size() - 1);
            }
        });
    }

    @Override
    public void onSendFail(MessageListData messageListData, String msg, int position) {
        Log.e(TAG, msg);
        listData.get(position).setSendStatus(MessageListData.SEND_FAIL);
        if (!messageListData.get_id().equals(listData.get(position).get_id())) {//位置变了,需要重新获取位置
            position = adapter.getPositionBy_id(messageListData.get_id());
        }
        adapter.notifyItemChanged(position);
        if (isResume) {
            mPresenter.updataDB(listData.get(position));
        }
    }

    @Override
    public void onGetDetailSuccess(BaseData<List<MessageListData>> model) {
        for (MessageListData messageListData : model.getData()) {
            messageListData.setUser_id(Constant.userData.getUser_id())
                    .setIs_read(0)
                    .setCli_id(listData.size() == 0 ? 0 : listData.get(listData.size() - 1).getCli_id() + 1);
            listData.add(messageListData);
            adapter.notifyItemInserted(listData.size() - 1);
            MessageDataUtil.insert(messageListData);
        }
        recycler_chat.scrollToPosition(listData.size() - 1);
        setAllMessageRead();
    }

    /**
     * 设置所有消息已读
     */
    public void setAllMessageRead() {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", Constant.userData == null ? "0" : Constant.userData.getToken())
                .addFormDataPart("put_id", get_id.equals(Constant.userData.getUser_id()) ? put_id : get_id)
                .addFormDataPart("get_id", Constant.userData.getUser_id());
        mPresenter.setMessageRead(builder.build().parts());
    }

    @Override
    public void onUpImgSuccess(BaseData<String> model, int position) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", Constant.userData == null ? "0" : Constant.userData.getToken())
                .addFormDataPart("put_id", put_id)
                .addFormDataPart("get_id", get_id)
                .addFormDataPart("type", String.valueOf(MessageListData.IMG))
                .addFormDataPart("img", model.getData());
        listData.get(position).setImg(model.getData());
        adapter.notifyItemChanged(position);
        mPresenter.sendMessage(builder.build().parts(), listData.get(position), position);
    }

    @Override
    public void onUpImgFail(String msg, int position) {
        super.onFail(msg);
        listData.get(position).setSendStatus(MessageListData.SEND_FAIL);
        adapter.notifyItemChanged(position);
        mPresenter.updataDB(listData.get(position));
    }

    @Override
    public void onUpAudioSuccess(BaseData<String> model, int position, AudioRecoderData data) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", Constant.userData == null ? "0" : Constant.userData.getToken())
                .addFormDataPart("put_id", put_id)
                .addFormDataPart("get_id", get_id)
                .addFormDataPart("length", String.valueOf(data.getEndTime() - data.getStartTime()))
                .addFormDataPart("type", String.valueOf(MessageListData.VOICE))
                .addFormDataPart("img", model.getData());
        mPresenter.sendMessage(builder.build().parts(), listData.get(position), position);
    }

    @Override
    public void onUpAudioFail(String msg, int position) {
        super.onFail(msg);
        listData.get(position).setSendStatus(MessageListData.SEND_FAIL);
        adapter.notifyItemChanged(position);
        mPresenter.updataDB(listData.get(position));
    }

    @Override
    public void onGetHistorySuccess(final List<MessageListData> model) {
        if (listData.size() == 0) {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("token", Constant.userData == null ? "0" : Constant.userData.getToken())
                    .addFormDataPart("put_id", put_id)
                    .addFormDataPart("get_id", get_id);
            if (model.size() > 0) {//有记录时获取未获取的新消息,无历史记录时获取所有新的消息
                builder.addFormDataPart("type", "new")
                        .addFormDataPart("start_id", String.valueOf(model.get(0).getId()));
            }
            mPresenter.getMessageDetail(builder.build().parts());
        }
        if (model.size() > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (MessageListData messageListData : model) {
                        listData.add(0, messageListData);
                        adapter.notifyItemInserted(0);
                    }
                    if (listData.size() == model.size()) {
                        recycler_chat.scrollToPosition(listData.size() - 1);
                    }
                }
            });
        }
        isLoadHistory = false;
    }

    @Override
    public void onGetHistoryFail(final String msg) {
        isLoadHistory = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showSingleToast(msg);
            }
        });
    }

    @Override
    public void onGetUserInfoSuccess(UserData model) {
        if (adapter != null) {
            adapter.setGetUserData(model);
            adapter.notifyDataSetChanged();
        }
        tv_tool.setText(model.getNickname());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                hideSoftInput(et_chat);
                finish();
                break;
            case R.id.img_type:
                hideSoftInput(et_chat);
                if (sendMode == TEXT) {
                    setMode(VOICE);
                } else if (sendMode == VOICE) {
                    setMode(TEXT);
                }
                if (ll_add_other.getVisibility() == View.VISIBLE) {
                    ll_add_other.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_send:
                sendTextMessage();
                break;
            case R.id.img_add:
                if (ll_add_other.getVisibility() == View.VISIBLE) {
                    setAddViewShow(false);
                } else {
                    if (sendMode == VOICE) {
                        setMode(TEXT);
                    }
                    setAddViewShow(true);
                }
                hideSoftInput(et_chat);
                moveListBottom();
                et_chat.clearFocus();
                break;
            case R.id.ll_img:
                hideSoftInput(et_chat);
                new MyDialogPhotoChooseFragment().setChooseNum(1).setOnPhotoChooseListener(new MyDialogPhotoChooseFragment.OnChooseLister() {
                    @Override
                    public void OnChoose(List<String> imgs, String tag) {
                        if (imgs.size() > 0) {
                            sendImgMessage(imgs.get(0));
                        }
                    }
                }).show(getSupportFragmentManager(), "img");
                break;
            case R.id.ll_take_photo:
                hideSoftInput(et_chat);
                new MyDialogPhotoChooseFragment().setChooseNum(1).setOnPhotoChooseListener(new MyDialogPhotoChooseFragment.OnChooseLister() {
                    @Override
                    public void OnChoose(List<String> imgs, String tag) {
                        if (imgs.size() > 0) {
                            sendImgMessage(imgs.get(0));
                        }
                    }
                }).setType(MyDialogPhotoChooseFragment.TAKE_PHOTO).show(getSupportFragmentManager(), "take_photo");
                break;
            case R.id.img_emoticon:

                break;
            case R.id.ll_voice_chat://发起语音聊天
                hideSoftInput(et_chat);
            if (adapter.getGetUserData() != null) {
                Intent intent = new Intent(this, VoiceChatService.class);
                intent.putExtra("name", adapter.getGetUserData().getNickname());
                intent.putExtra("img", adapter.getGetUserData().getImg_header());
                intent.putExtra("port", -1);
                intent.putExtra("apply_id", put_id);
                intent.putExtra("recevice_id", get_id);
                intent.putExtra("apply", true);
                startService(intent);
                break;
            }else {
                ToastUtils.showSingleToast("服务器繁忙!请稍后再试");
            }
        }
    }

    public void setMode(int mode) {
        if (mode == VOICE) {
            img_type.setImageResource(R.drawable.keyboard);
            img_emoticon.setVisibility(View.GONE);
            et_chat.setVisibility(View.GONE);
            tv_voice.setVisibility(View.VISIBLE);
            tv_send.setVisibility(View.GONE);
            sendMode = VOICE;
        } else if (mode == TEXT) {
            img_type.setImageResource(R.drawable.voice);
            img_emoticon.setVisibility(View.VISIBLE);
            et_chat.setVisibility(View.VISIBLE);
            tv_voice.setVisibility(View.GONE);
            if (et_chat.getText().toString().trim().length() > 0) {
                tv_send.setVisibility(View.VISIBLE);
            }
            sendMode = TEXT;
        }
    }

    /**
     * 设置添加照片栏的显示
     *
     * @param isShow 是否显示
     */
    public void setAddViewShow(boolean isShow) {
        if (isShow) {
            ll_add_other.setVisibility(View.VISIBLE);
        } else {
            ll_add_other.setVisibility(View.GONE);
        }
    }


    /**
     * 发送文本消息
     */
    public synchronized void sendTextMessage() {
        if (et_chat.getText().toString().trim().length() == 0) {
            ToastUtils.showSingleToast("请输入发送的内容");
            return;
        }
        //发送文本信息
        MessageListData data = new MessageListData();
        data.setContent(StringHandleUtils.deleteEnter(et_chat.getText().toString().trim()));
        data.setType(TEXT);
        data.set_id(System.nanoTime());
        data.setUser_id(Constant.userData.getUser_id());
        data.setPut_id(put_id);
        data.setGet_id(get_id).setIs_read(0)
                .setCli_id(listData.size() == 0 ? 0 : listData.get(listData.size() - 1).getCli_id() + 1);
        data.setImg_header(Constant.userData.getImg_header());
        data.setSendStatus(MessageListData.SENDING);
        data.setNickname(Constant.userData.getNickname());
        listData.add(data);
        adapter.notifyItemChanged(listData.size() - 1);
        moveListBottom();
        et_chat.setText("");
        mPresenter.insertDB(data);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", Constant.userData == null ? "0" : Constant.userData.getToken())
                .addFormDataPart("put_id", put_id)
                .addFormDataPart("get_id", get_id)
                .addFormDataPart("type", String.valueOf(TEXT))
                .addFormDataPart("content", data.getContent());
        mPresenter.sendMessage(builder.build().parts(), data, listData.size() - 1);

    }

    /**
     * 发送图片信息
     *
     * @param path
     */
    public synchronized void sendImgMessage(final String path) {
        MessageListData messageListData = new MessageListData();
        messageListData.setType(MessageListData.IMG).setImg(path).setIs_read(0)
                .set_id(System.nanoTime())
                .setCli_id(listData.size() == 0 ? 0 : listData.get(listData.size() - 1).getCli_id() + 1)
                .setPut_id(put_id).setGet_id(get_id).setImg_header(Constant.userData.getImg_header())
                .setUser_id(Constant.userData.getUser_id())
                .setSendStatus(MessageListData.SENDING);
        listData.add(messageListData);
        mPresenter.insertDB(messageListData);
        final int position = listData.size() - 1;
        adapter.notifyItemInserted(position);
        moveListBottom();
        new Compressor(this)
                .compressToFileAsFlowable(new File(path))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {

                        mPresenter.upImg(new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("token", Constant.userData == null ? "0" : Constant.userData.getToken())
                                .addFormDataPart("id", Constant.userData.getUser_id())
                                .addFormDataPart("path", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file))
                                .build().parts(), position);
                    }
                });
    }

    /**
     * 发送语音信息
     *
     * @param audioRecoderData
     */
    public synchronized void sendVoiceMessage(AudioRecoderData audioRecoderData) {
        MessageListData messageListData = new MessageListData();
        messageListData.setType(MessageListData.VOICE).setImg(audioRecoderData.getFilePath())
                .set_id(System.nanoTime())
                .setPut_id(put_id).setGet_id(get_id).setImg_header(Constant.userData.getImg_header())
                .setLength((int) (audioRecoderData.getEndTime() - audioRecoderData.getStartTime()))
                .setUser_id(Constant.userData.getUser_id())
                .setIs_read(0)
                .setCli_id(listData.size() == 0 ? 0 : listData.get(listData.size() - 1).getCli_id() + 1)
                .setSendStatus(MessageListData.SENDING);
        listData.add(messageListData);
        mPresenter.insertDB(messageListData);
        final int position = listData.size() - 1;
        adapter.notifyItemInserted(position);
        moveListBottom();

        File file = new File(audioRecoderData.getFilePath());
        Log.e(TAG, file.getAbsolutePath());

        mPresenter.upAudio(new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", Constant.userData == null ? "0" : Constant.userData.getToken())
                .addFormDataPart("id", Constant.userData.getUser_id())
                .addFormDataPart("path", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .build().parts(), position, audioRecoderData);
    }

    /**
     * 重新发送消息
     *
     * @param position
     */
    public synchronized void reSendMessage(final int position) {
        MessageListData data = listData.get(position);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", Constant.userData == null ? "0" : Constant.userData.getToken())
                .addFormDataPart("put_id", put_id)
                .addFormDataPart("get_id", get_id);
        switch (data.getType()) {
            case MessageListData.TEXT:
                builder.addFormDataPart("type", String.valueOf(TEXT))
                        .addFormDataPart("content", data.getContent());
                mPresenter.sendMessage(builder.build().parts(), data, position);
                break;
            case MessageListData.IMG:
                if (data.getImg().startsWith("http")) {//图片已上传,直接重发
                    builder.addFormDataPart("type", String.valueOf(MessageListData.IMG))
                            .addFormDataPart("img", data.getImg());
                    mPresenter.sendMessage(builder.build().parts(), data, position);
                } else {
                    File ImgFile = new File(data.getImg());
                    if (!fileIsExist(ImgFile)){
                        ToastUtils.showSingleToast("图片已不存在");
                        return;
                    }
                    new Compressor(this)
                            .compressToFileAsFlowable(ImgFile)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .subscribe(new Consumer<File>() {
                                @Override
                                public void accept(File file) throws Exception {

                                    mPresenter.upImg(new MultipartBody.Builder()
                                            .setType(MultipartBody.FORM)
                                            .addFormDataPart("token", Constant.userData == null ? "0" : Constant.userData.getToken())
                                            .addFormDataPart("id", Constant.userData.getUser_id())
                                            .addFormDataPart("path", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file))
                                            .build().parts(), position);
                                }
                            });
                }
                break;
            case MessageListData.VOICE:
                if (data.getImg().startsWith("http")) {//音频已上传,直接重发
                    builder.addFormDataPart("length", String.valueOf(data.getLength()))
                            .addFormDataPart("type", String.valueOf(MessageListData.VOICE))
                            .addFormDataPart("img", data.getImg());
                    mPresenter.sendMessage(builder.build().parts(), data, position);
                } else {
                    File file = new File(data.getImg());
                    if (!fileIsExist(file)){
                        ToastUtils.showSingleToast("语音文件已不存在");
                        return;
                    }
                    AudioRecoderData audioRecoderData = new AudioRecoderData();
                    audioRecoderData.setFilePath(data.getImg());
                    audioRecoderData.setStartTime(0);
                    audioRecoderData.setEndTime(data.getLength());
                    mPresenter.upAudio(new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("token", Constant.userData == null ? "0" : Constant.userData.getToken())
                            .addFormDataPart("id", Constant.userData.getUser_id())
                            .addFormDataPart("path", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file))
                            .build().parts(), position, audioRecoderData);
                }
                break;
        }
        data.setSendStatus(MessageListData.SENDING);
        mPresenter.updataDB(data);
        adapter.notifyItemChanged(position);

    }

    public boolean fileIsExist(File file){
        if (file.exists()){
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (ll_add_other.getVisibility() == View.VISIBLE) {
            setAddViewShow(false);
            return;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioRecoderManager.getInstance().destroy(getApplicationContext());
        AudioPlayManager.getInstance().destory();
        if (iMessageGet != null) {
            MessageCenter.getInstance().removeMessageHandle(iMessageGet);
        }
    }
}
