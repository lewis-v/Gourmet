package com.yw.gourmet.ui.search.keySearch;

import android.content.Intent;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.ShareListAdapter;
import com.yw.gourmet.adapter.UserAdapter;
import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.data.UserData;
import com.yw.gourmet.dialog.MyDialogMoreFragment;
import com.yw.gourmet.listener.OnItemClickListener;
import com.yw.gourmet.listener.OnMoreListener;
import com.yw.gourmet.listener.OnReMarkListener;
import com.yw.gourmet.ui.detail.common.CommonDetailActivity;
import com.yw.gourmet.ui.detail.diary.DiaryDetailActivity;
import com.yw.gourmet.ui.detail.menu.MenuDetailActivity;
import com.yw.gourmet.ui.detail.raiders.RaidersDetailActivity;
import com.yw.gourmet.ui.personal.PersonalActivity;
import com.yw.gourmet.utils.SoftInputUtils;
import com.yw.gourmet.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MultipartBody;

public class KeySearchActivity extends BaseActivity<KeySearchPresenter> implements KeySearchContract.View {
    private AppCompatSpinner spinner_type;
    private EditText et_search;
    private TextView tv_cancel;
    private RecyclerView recycler_friend,recycler_other;
    private ShareListAdapter shareListAdapter;
    private UserAdapter userAdapter;
    private List<ShareListData<List<String>>> shareListData = new ArrayList<>();
    private List<ShareListData> list = new ArrayList<>();
    private List<String> spinner = Arrays.asList("找朋友","分享","日记","食谱","攻略");

    @Override
    public void onSearchSuccess(BaseData<List<ShareListData<List<String>>>> model) {
        setLoadDialog(false);
        recycler_other.setVisibility(View.VISIBLE);
        shareListData.clear();
        shareListData.addAll(model.getData());
        shareListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSearchUserSuccess(BaseData<List<ShareListData<List<String>>>> model) {
        setLoadDialog(false);
        recycler_friend.setVisibility(View.VISIBLE);
        list.clear();
        list.addAll(model.getData());
        userAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSearchFail(String msg) {
        super.onFail(msg);
        recycler_friend.setVisibility(View.GONE);
    }

    @Override
    public void onSearchUserFail(String msg) {
        super.onFail(msg);
        recycler_friend.setVisibility(View.GONE);
    }

    @Override
    public void onReMarkSuccess(BaseData<ShareListData<List<String>>> model, int position) {
        setLoadDialog(false);
        shareListData.set(position,model.getData());
        shareListAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_key_search;
    }

    @Override
    protected void initView() {
        spinner_type = findViewById(R.id.spinner_type);
        spinner_type.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,spinner));

        et_search = findViewById(R.id.et_search);
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    if (et_search.getText().toString().trim().isEmpty()){
                        ToastUtils.showSingleToast("请输入搜索关键字");
                    }else {
                        MultipartBody.Builder builder = new MultipartBody.Builder()
                                .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("key",et_search.getText().toString());
                        if (Constant.userData != null){
                            builder.addFormDataPart("user_id",Constant.userData.getUser_id());
                        }
                        switch (spinner_type.getSelectedItem().toString()){
                            case "找朋友":
                                builder.addFormDataPart("type", "-1");
                                mPresenter.searchUser(builder.build().parts());
                                break;
                            case "分享":
                                builder.addFormDataPart("type", String.valueOf(Constant.TypeFlag.SHARE));
                                mPresenter.search(builder.build().parts());
                                break;
                            case "日记":
                                builder.addFormDataPart("type", String.valueOf(Constant.TypeFlag.DIARY));
                                mPresenter.search(builder.build().parts());
                                break;
                            case "食谱":
                                builder.addFormDataPart("type", String.valueOf(Constant.TypeFlag.MENU));
                                mPresenter.search(builder.build().parts());
                                break;
                            case "攻略":
                                builder.addFormDataPart("type", String.valueOf(Constant.TypeFlag.RAIDERS));
                                mPresenter.search(builder.build().parts());
                                break;
                        }
                        setLoadDialog(true);
                        SoftInputUtils.hideSoftInput(et_search);
                        recycler_friend.setVisibility(View.GONE);
                        recycler_other.setVisibility(View.GONE);
                    }
                    return true;
                }
                return false;
            }
        });

        tv_cancel = findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recycler_friend = findViewById(R.id.recycler_friend);
        userAdapter = new UserAdapter(this,list);
        recycler_friend.setItemAnimator(new DefaultItemAnimator());
        recycler_friend.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recycler_friend.setAdapter(userAdapter);

        userAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClick(View v, int position) {
                Intent intent = new Intent(KeySearchActivity.this, PersonalActivity.class);
                intent.putExtra("id",list.get(position).getUser_id());
                startActivity(intent);
            }

            @Override
            public boolean OnLongClick(View v, int position) {
                return false;
            }
        });

        recycler_other = findViewById(R.id.recycler_other);
        recycler_other.setItemAnimator(new DefaultItemAnimator());
        recycler_other.setLayoutManager(new LinearLayoutManager(this));
        shareListAdapter = new ShareListAdapter(this,shareListData,getSupportFragmentManager());
        recycler_other.setAdapter(shareListAdapter);
        shareListAdapter.setListener(new OnItemClickListener() {
            @Override
            public void OnClick(View v, int position) {
                Intent intent = null;
                switch (shareListData.get(position).getType()) {
                    case Constant.TypeFlag.SHARE:
                        intent = new Intent(KeySearchActivity.this, CommonDetailActivity.class);
                        break;
                    case Constant.TypeFlag.DIARY:
                        intent = new Intent(KeySearchActivity.this, DiaryDetailActivity.class);
                        break;
                    case Constant.TypeFlag.MENU:
                        intent = new Intent(KeySearchActivity.this, MenuDetailActivity.class);
                        break;
                    case Constant.TypeFlag.RAIDERS:
                        intent = new Intent(KeySearchActivity.this, RaidersDetailActivity.class);
                        break;
                }
                if (intent != null){
                    intent.putExtra("id",shareListData.get(position).getId());
                    intent.putExtra("type",String.valueOf(shareListData.get(position).getType()));
                    startActivity(intent);
                }

            }

            @Override
            public boolean OnLongClick(View v, int position) {
                return false;
            }
        });
        shareListAdapter.setOnMoreListener(new OnMoreListener() {
            @Override
            public void OnMoreClick(View view, int position) {
                MyDialogMoreFragment myDialogMoreFragment = new MyDialogMoreFragment()
                        .setId(shareListData.get(position).getId());
                switch (shareListData.get(position).getType()) {
                    case Constant.TypeFlag.SHARE://普通分享
                        myDialogMoreFragment.setShare(false).setType(shareListData.get(position).getType())
                                .show(getSupportFragmentManager(), "share");
                        break;
                    case Constant.TypeFlag.DIARY://日记分享
                    case Constant.TypeFlag.MENU://食谱分享
                    case Constant.TypeFlag.RAIDERS://攻略分享
                        myDialogMoreFragment.setShareCoverUrl(shareListData.get(position).getCover())
                                .setId(shareListData.get(position).getId())
                                .setType(shareListData.get(position).getType())
                                .setShareDescription(shareListData.get(position).getContent())
                                .setShareTitle(shareListData.get(position).getTitle())
                                .setShareUrl(Api.API_BASE_URL + "/Share/Other?id="
                                        + shareListData.get(position).getId() + "&type=" + shareListData.get(position).getType())
                                .show(getSupportFragmentManager(), "share");
                        break;
                }
            }
        });
        shareListAdapter.setOnReMarkListener(new OnReMarkListener() {
            @Override
            public void OnGoodClick(View view, int position) {
                if (Constant.userData == null){
                    ToastUtils.showSingleToast("请登陆后再进行操作");
                }else {
                    setLoadDialog(true);
                    MultipartBody.Builder builder = new MultipartBody.Builder()
                            .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("id", Constant.userData.getUser_id())
                            .addFormDataPart("type",shareListData.get(position).getType()+"")
                            .addFormDataPart("act_id",shareListData.get(position).getId())
                            .addFormDataPart("act","1");
                    mPresenter.reMark(builder.build().parts(),position);
                }
            }

            @Override
            public void OnBadClick(View view, int position) {
                if (Constant.userData == null){
                    ToastUtils.showSingleToast("请登陆后再进行操作");
                }else {
                    setLoadDialog(true);
                    MultipartBody.Builder builder = new MultipartBody.Builder()
                            .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("id", Constant.userData.getUser_id())
                            .addFormDataPart("type",shareListData.get(position).getType()+"")
                            .addFormDataPart("act_id",shareListData.get(position).getId())
                            .addFormDataPart("act","0");
                    mPresenter.reMark(builder.build().parts(),position);
                }
            }

            @Override
            public void OnCommentClick(View view, int position) {
                Intent intent = null;
                switch (shareListData.get(position).getType()) {
                    case Constant.TypeFlag.SHARE:
                        intent = new Intent(KeySearchActivity.this, CommonDetailActivity.class);
                        break;
                    case Constant.TypeFlag.DIARY:
                        intent = new Intent(KeySearchActivity.this, DiaryDetailActivity.class);
                        break;
                    case Constant.TypeFlag.MENU:
                        intent = new Intent(KeySearchActivity.this, MenuDetailActivity.class);
                        break;
                    case Constant.TypeFlag.RAIDERS:
                        intent = new Intent(KeySearchActivity.this, RaidersDetailActivity.class);
                        break;
                }
                if (intent != null){
                    intent.putExtra("id",shareListData.get(position).getId());
                    intent.putExtra("type",String.valueOf(shareListData.get(position).getType()));
                    intent.putExtra("isComment",true);
                    startActivity(intent);
                }
            }
        });

        int type = getIntent().getIntExtra("type",-2);
        switch (type){
            case Constant.TypeFlag.DIARY:
                spinner_type.setSelection(2);
                break;
            case Constant.TypeFlag.MENU:
                spinner_type.setSelection(3);
                break;
            case Constant.TypeFlag.RAIDERS:
                spinner_type.setSelection(4);
                break;
            case -1://本地搜索

                break;
        }
    }
}
