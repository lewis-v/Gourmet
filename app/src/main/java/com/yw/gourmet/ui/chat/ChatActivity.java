package com.yw.gourmet.ui.chat;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.ChatAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.MessageListData;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;

public class ChatActivity extends BaseActivity<ChatPresenter> implements ChatContract.View
        ,View.OnClickListener{
    private final static int TEXT = 0;//发送文本模式
    private final static int VOICE = 1;//发送语音模式

    private ImageView img_back,img_type,img_add,img_emoticon;
    private RecyclerView recycler_chat;
    private EditText et_chat;
    private TextView tv_voice,tv_tool;
    private int sendMode = 0;//发送的模式,默认为文本
    private ChatAdapter adapter;
    private List<MessageListData> listData = new ArrayList<>();
    private String get_id,put_id;//接收者id与发送者id

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initView() {
        view_parent = findViewById(R.id.view_parent);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

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

        et_chat = (EditText) findViewById(R.id.et_chat);
        et_chat.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideSoftInput(et_chat);
                    //发送文本信息
                    MessageListData data = new MessageListData();
                    data.setContent(v.getText().toString());
                    data.setType(TEXT);
                    data.setPut_id(put_id);
                    data.setGet_id(get_id);
                    data.setImg_header(Constant.userData.getImg_header());
                    data.setSendStatus(MessageListData.SENDING);
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
                    return true;
                }
                return false;
            }
        });

        tv_voice = (TextView) findViewById(R.id.tv_voice);
        tv_tool = (TextView) findViewById(R.id.tv_tool);

        tv_voice.setOnClickListener(this);
        tv_tool.setText(getIntent().getStringExtra("nickname"));

        put_id = getIntent().getStringExtra("put_id");
        get_id = getIntent().getStringExtra("get_id");

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", Constant.userData.getToken())
                .addFormDataPart("put_id", put_id)
                .addFormDataPart("get_id", get_id);
        mPresenter.getMessageDetail(builder.build().parts());

    }

    @Override
    public void onSendSuccess(int position) {
        listData.get(position).setSendStatus(MessageListData.SEND_SUCCESS);
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onSendFail(String msg, int position) {
        listData.get(position).setSendStatus(MessageListData.SEND_FAIL);
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onGetDetailSuccess(BaseData<List<MessageListData>> model) {
        listData.clear();
        listData.addAll(model.getData());
        adapter.notifyDataSetChanged();
        recycler_chat.scrollToPosition(listData.size() - 1);
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
                    img_type.setImageResource(R.drawable.voice);
                    img_emoticon.setVisibility(View.GONE);
                    et_chat.setVisibility(View.GONE);
                    tv_voice.setVisibility(View.VISIBLE);
                    sendMode = VOICE;
                }else if (sendMode == VOICE){
                    img_type.setImageResource(R.drawable.keyboard);
                    img_emoticon.setVisibility(View.VISIBLE);
                    et_chat.setVisibility(View.VISIBLE);
                    tv_voice.setVisibility(View.GONE);
                    sendMode = TEXT;
                }
                break;
            case R.id.img_add:

                break;
            case R.id.tv_voice:

                break;
            case R.id.img_emoticon:

                break;
        }
    }

    /**
     * 隐藏对应控件的软键盘
     * @param view
     */
    public void hideSoftInput(View view){
        InputMethodManager imm = (InputMethodManager) view
                .getContext().getApplicationContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }catch (Exception e){}
    }
}
