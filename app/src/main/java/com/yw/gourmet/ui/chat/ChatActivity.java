package com.yw.gourmet.ui.chat;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseActivity;

public class ChatActivity extends BaseActivity implements ChatContract.View,View.OnClickListener{
    private final static int TEXT = 0;//发送文本模式
    private final static int VOICE = 1;//发送语音模式

    private ImageView img_back,img_type,img_add,img_emoticon;
    private RecyclerView recycler_chat;
    private EditText et_chat;
    private TextView tv_voice,tv_tool;
    private int sendMode = 0;//发送的模式,默认为文本

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initView() {
        toolbar = findViewById(R.id.toolbar);

        img_back = (ImageView)findViewById(R.id.img_back);
        img_type = (ImageView)findViewById(R.id.img_type);
        img_add = (ImageView)findViewById(R.id.img_add);
        img_emoticon = (ImageView)findViewById(R.id.img_emoticon);

        img_emoticon.setOnClickListener(this);
        img_add.setOnClickListener(this);
        img_back.setOnClickListener(this);
        img_type.setOnClickListener(this);

        recycler_chat = (RecyclerView)findViewById(R.id.recycler_chat);

        et_chat = (EditText)findViewById(R.id.et_chat);
        et_chat.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    try {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }catch (Exception e){}
                    //发送文本信息
                    return true;
                }
                return false;
            }
        });

        tv_voice = (TextView)findViewById(R.id.tv_voice);
        tv_tool = (TextView)findViewById(R.id.tv_tool);

        tv_voice.setOnClickListener(this);
    }

    @Override
    public void onSendSuccess() {

    }

    @Override
    public void onClick(View v) {
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
                    InputMethodManager imm = (InputMethodManager) et_chat
                            .getContext().getApplicationContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    try {
                        imm.hideSoftInputFromWindow(et_chat.getApplicationWindowToken(), 0);
                    }catch (Exception e){}
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
}
