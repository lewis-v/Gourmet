package com.yw.gourmet.ui.chat;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.ChatAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.center.MessageCenter;
import com.yw.gourmet.center.event.IMessageGet;
import com.yw.gourmet.dao.data.messageData.MessageDataUtil;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.MessageListData;
import com.yw.gourmet.dialog.MyDialogPhotoChooseFragment;
import com.yw.gourmet.dialog.MyDialogPhotoShowFragment;
import com.yw.gourmet.ui.share.common.CommonShareActivity;
import com.yw.gourmet.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.yw.gourmet.utils.SoftInputUtils.hideSoftInput;

public class ChatActivity extends BaseActivity<ChatPresenter> implements ChatContract.View
        ,View.OnClickListener{
    private final static String TAG = "ChatActivity";
    private final static int TEXT = 0;//发送文本模式
    private final static int VOICE = 1;//发送语音模式

    private ImageView img_back,img_type,img_add,img_emoticon;
    private RecyclerView recycler_chat;
    private EditText et_chat;
    private TextView tv_voice,tv_tool,tv_send;
    private int sendMode = 0;//发送的模式,默认为文本
    private ChatAdapter adapter;
    private List<MessageListData> listData = new ArrayList<>();
    private String get_id,put_id;//接收者id与发送者id
    private IMessageGet iMessageGet;
    private LinearLayout ll_take_photo,ll_img,ll_add_other;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initView() {
        view_parent = findViewById(R.id.view_parent);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        ll_add_other = findViewById(R.id.ll_add_other);
        ll_img = findViewById(R.id.ll_img);
        ll_take_photo = findViewById(R.id.ll_take_photo);

        ll_take_photo.setOnClickListener(this);
        ll_img.setOnClickListener(this);

        img_back = (ImageView) findViewById(R.id.img_back);
        img_type = (ImageView) findViewById(R.id.img_type);
        img_add = (ImageView) findViewById(R.id.img_add);
        img_emoticon = (ImageView) findViewById(R.id.img_emoticon);

        img_emoticon.setOnClickListener(this);
        img_add.setOnClickListener(this);
        img_back.setOnClickListener(this);
        img_type.setOnClickListener(this);

        recycler_chat = (RecyclerView) findViewById(R.id.recycler_chat);
        recycler_chat.setItemAnimator(new DefaultItemAnimator());
        recycler_chat.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(this, listData);
        recycler_chat.setAdapter(adapter);
        adapter.setOnImgClickListener(new ChatAdapter.OnImgClickListener() {
            @Override
            public void onClick(View view, int position) {
                new MyDialogPhotoShowFragment().addImgString(listData.get(position).getImg()).show(getSupportFragmentManager(),"img");
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
                if (hasFocus){
                    if (ll_add_other.getVisibility() == View.VISIBLE){
                        setAddViewShow(false);
                    }
                    recycler_chat.smoothScrollToPosition(listData.size()-1);
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
                if (et_chat.getText().toString().trim().length()>0){
                    if (tv_send.getVisibility() != View.VISIBLE) {
                        tv_send.setVisibility(View.VISIBLE);
                    }
                }else {
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

        tv_voice.setOnClickListener(this);
        tv_tool.setText(getIntent().getStringExtra("nickname"));
        tv_send.setOnClickListener(this);

        put_id = getIntent().getStringExtra("put_id");
        get_id = getIntent().getStringExtra("get_id");

        mPresenter.getHistory(put_id,get_id,0);

        iMessageGet = new IMessageGet() {
            @Override
            public boolean onGetMessage(final MessageListData message) {
                if (message.getPut_id().equals(get_id)) {
                    message.setUser_id(Constant.userData.getUser_id())
                            .setIs_read(0)
                            .setCli_id(listData.size() == 0?0:listData.get(listData.size()-1).getCli_id()+1);
                    mPresenter.insertDB(message);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listData.add(message);
                            adapter.notifyItemInserted(listData.size() - 1);
                            recycler_chat.smoothScrollToPosition(listData.size()-1);
                            MultipartBody.Builder builder1 = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("id",message.getId());
                            mPresenter.setMessageRead(builder1.build().parts());
                        }
                    });
                    return true;
                }
                return false;
            }
        };
        MessageCenter.getInstance().addMessageHandleTop(iMessageGet);
    }

    /**
     * 发送文本消息
     */
    public void sendTextMessage(){
        if (et_chat.getText().toString().trim().length() == 0){
            ToastUtils.showSingleToast("请输入发送的内容");
            return;
        }
        hideSoftInput(et_chat);
        //发送文本信息
        MessageListData data = new MessageListData();
        data.setContent(et_chat.getText().toString());
        data.setType(TEXT);
        data.setPut_id(put_id);
        data.setGet_id(get_id);
        data.setImg_header(Constant.userData.getImg_header());
        data.setSendStatus(MessageListData.SENDING);
        data.setNickname(Constant.userData.getNickname());
        listData.add(data);
        adapter.notifyItemChanged(listData.size() - 1);
        recycler_chat.smoothScrollToPosition(listData.size() - 1);
        et_chat.setText("");
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token",Constant.userData.getToken())
                .addFormDataPart("put_id",put_id)
                .addFormDataPart("get_id",get_id)
                .addFormDataPart("type",String.valueOf(TEXT))
                .addFormDataPart("content",data.getContent());
        mPresenter.sendMessage(builder.build().parts(),listData.size()-1);

    }

    @Override
    public void onSendSuccess(int position) {
        listData.get(position).setSendStatus(MessageListData.SEND_SUCCESS);
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onSendFail(String msg, int position) {
        Log.e(TAG,msg);
        listData.get(position).setSendStatus(MessageListData.SEND_FAIL);
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onGetDetailSuccess(BaseData<List<MessageListData>> model) {
        for (MessageListData messageListData : model.getData()){
            messageListData.setUser_id(Constant.userData.getUser_id())
                    .setIs_read(0)
                    .setCli_id(listData.size() == 0?0:listData.get(listData.size()-1).getCli_id()+1);
            listData.add(messageListData);
            adapter.notifyItemInserted(listData.size()-1);
            MessageDataUtil.insert(messageListData);
        }
        recycler_chat.scrollToPosition(listData.size() - 1);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("put_id",get_id)
                .addFormDataPart("get_id",put_id);//此处的接收者和发送者位置对换,因为要设置我接受的信息已读
        mPresenter.setMessageRead(builder.build().parts());
    }

    @Override
    public void onUpImgSuccess(BaseData<String> model, int position) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .addFormDataPart("token",Constant.userData.getToken())
                .addFormDataPart("put_id",put_id)
                .addFormDataPart("get_id",get_id)
                .addFormDataPart("type",String.valueOf(MessageListData.IMG))
                .addFormDataPart("img",model.getData());
        mPresenter.sendMessage(builder.build().parts(),position);
    }

    @Override
    public void onUpImgFail(String msg, int position) {
        super.onFail(msg);
        listData.get(position).setSendStatus(MessageListData.SEND_FAIL);
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onGetHistorySuccess(final List<MessageListData> model) {
        if (listData.size() == 0){
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("token", Constant.userData.getToken())
                    .addFormDataPart("put_id", put_id)
                    .addFormDataPart("get_id", get_id);
            if (model.size() > 0){//有记录时获取未获取的新消息,无历史记录时获取所有新的消息
                   builder.addFormDataPart("type","new")
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
                    recycler_chat.scrollToPosition(listData.size() - 1);
                }
            });
        }
    }

    @Override
    public void onGetHistoryFail(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showSingleToast(msg);
            }
        });
    }

    @Override
    public void onClick(View v) {
        hideSoftInput(et_chat);
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.img_type:
                if (sendMode == TEXT){
                    img_type.setImageResource(R.drawable.keyboard);
                    img_emoticon.setVisibility(View.GONE);
                    et_chat.setVisibility(View.GONE);
                    tv_voice.setVisibility(View.VISIBLE);
                    tv_send.setVisibility(View.GONE);
                    sendMode = VOICE;
                }else if (sendMode == VOICE){
                    img_type.setImageResource(R.drawable.voice);
                    img_emoticon.setVisibility(View.VISIBLE);
                    et_chat.setVisibility(View.VISIBLE);
                    tv_voice.setVisibility(View.GONE);
                    if (et_chat.getText().toString().trim().length()>0){
                        tv_send.setVisibility(View.VISIBLE);
                    }
                    sendMode = TEXT;
                }
                if (ll_add_other.getVisibility() == View.VISIBLE){
                    ll_add_other.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_send:
                sendTextMessage();
                break;
            case R.id.img_add:
                if (ll_add_other.getVisibility() == View.VISIBLE){
                    setAddViewShow(false);
                }else {
                    setAddViewShow(true);
                }
                hideSoftInput(et_chat);
                et_chat.clearFocus();
                break;
            case R.id.ll_img:
                new MyDialogPhotoChooseFragment().setChooseNum(1).setOnPhotoChooseListener(new MyDialogPhotoChooseFragment.OnChooseLister() {
                    @Override
                    public void OnChoose(List<String> imgs, String tag) {
                        if (imgs.size()>0){
                            sendImgMessage(imgs.get(0));
                        }
                    }
                }).show(getSupportFragmentManager(),"img");
                break;
            case R.id.ll_take_photo:
                new MyDialogPhotoChooseFragment().setChooseNum(1).setOnPhotoChooseListener(new MyDialogPhotoChooseFragment.OnChooseLister() {
                    @Override
                    public void OnChoose(List<String> imgs, String tag) {
                        if (imgs.size()>0){
                            sendImgMessage(imgs.get(0));
                        }
                    }
                }).setType(MyDialogPhotoChooseFragment.TAKE_PHOTO).show(getSupportFragmentManager(),"take_photo");
                break;
            case R.id.tv_voice:

                break;
            case R.id.img_emoticon:

                break;
        }
    }

    /**
     * 设置添加照片栏的显示
     * @param isShow 是否显示
     */
    public void setAddViewShow(boolean isShow){
        if (isShow){
            ll_add_other.setVisibility(View.VISIBLE);
        }else {
            ll_add_other.setVisibility(View.GONE);
        }
    }

    /**
     * 发送图片信息
     * @param path
     */
    public void sendImgMessage(final String path){
        MessageListData messageListData = new MessageListData();
        messageListData.setType(MessageListData.IMG).setImg(path)
                .setPut_id(put_id).setGet_id(get_id).setImg_header(Constant.userData.getImg_header())
                .setSendStatus(MessageListData.SENDING);
        listData.add(messageListData);
        final int position = listData.size() -1;
        adapter.notifyItemInserted(position);
        recycler_chat.smoothScrollToPosition(position);
        new Compressor(this)
                .compressToFileAsFlowable(new File(path))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {

                        mPresenter.upImg(new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("id", Constant.userData.getUser_id())
                                .addFormDataPart("path", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file))
                                .build().parts(), position);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (ll_add_other.getVisibility() == View.VISIBLE){
            setAddViewShow(false);
            return;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (iMessageGet != null){
            MessageCenter.getInstance().removeMessageHandle(iMessageGet);
        }
    }
}
