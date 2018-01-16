package com.yw.gourmet.ui.share.raiders;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.IngredientAdapter;
import com.yw.gourmet.adapter.RaidersListAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.RaidersListData;
import com.yw.gourmet.dialog.MyDialogEditFragment;
import com.yw.gourmet.dialog.MyDialogPhotoChooseFragment;
import com.yw.gourmet.dialog.MyDialogRaidersListFragment;
import com.yw.gourmet.listener.OnAddListener;
import com.yw.gourmet.listener.OnDeleteListener;
import com.yw.gourmet.listener.OnEditDialogEnterClickListener;
import com.yw.gourmet.listener.OnItemClickListener;
import com.yw.gourmet.ui.share.menu.MenuActivity;
import com.yw.gourmet.utils.ToastUtils;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RaidersActivity extends BaseActivity<RaidersPresenter> implements View.OnClickListener
,RaidersContract.View{
    private EditText et_title,et_introduction;
    private TextView tv_cancel,tv_send,tv_power;
    private RecyclerView recycler_tag,recycler_raiders_list;
    private IngredientAdapter tagAdapter;
    private RaidersListAdapter raidersListAdapter;
    private List<RaidersListData<List<String>>> raidersListData = new ArrayList<>();
    private List<String> tagList = new ArrayList<>();
    private int status = 1;//权限,公开或私有,1公开,0私有,默认公开

    @Override
    protected int getLayoutId() {
        return R.layout.activity_raiders;
    }

    @Override
    protected void initView() {
        et_title = findViewById(R.id.et_title);
        et_introduction = findViewById(R.id.et_introduction);

        tv_cancel = findViewById(R.id.tv_cancel);
        tv_send = findViewById(R.id.tv_send);
        tv_power = findViewById(R.id.tv_power);

        tv_cancel.setOnClickListener(this);
        tv_send.setOnClickListener(this);
        tv_power.setOnClickListener(this);

        recycler_tag = findViewById(R.id.recycler_tag);
        recycler_tag.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.HORIZONTAL));
        recycler_tag.setItemAnimator(new DefaultItemAnimator());
        tagAdapter = new IngredientAdapter(this,tagList,true);
        tagAdapter.setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void OnDelete(View v, int position) {
                tagList.remove(position);
                tagAdapter.notifyItemRemoved(position);
            }
        });
        tagAdapter.setOnAddListener(new OnAddListener() {
            @Override
            public void OnAdd(View view, final int position) {
                new MyDialogEditFragment().setEtHint("请输入标签、类型").setOnEditDialogEnterClickListener(new OnEditDialogEnterClickListener() {
                    @Override
                    public void OnClick(String edit, String tag) {
                        tagList.add(edit);
                        tagAdapter.notifyItemInserted(position);
                    }
                }).show(getSupportFragmentManager(),"type");
            }
        });
        recycler_tag.setAdapter(tagAdapter);
        tagAdapter.notifyDataSetChanged();

        recycler_raiders_list = findViewById(R.id.recycler_raiders_list);
        recycler_raiders_list.setNestedScrollingEnabled(false);
        recycler_raiders_list.setItemAnimator(new DefaultItemAnimator());
        recycler_raiders_list.setLayoutManager(new LinearLayoutManager(this));
        raidersListAdapter = new RaidersListAdapter(this,raidersListData,true);
        recycler_raiders_list.setAdapter(raidersListAdapter);
        raidersListAdapter.notifyDataSetChanged();

        raidersListAdapter.setOnAddListener(new OnAddListener() {
            @Override
            public void OnAdd(View view, final int position) {
                new MyDialogRaidersListFragment().setOnEnterListener(new MyDialogRaidersListFragment.OnEnterListener() {
                    @Override
                    public void onEnter(RaidersListData<List<String>> raidersListData, String Tag) {
                        RaidersActivity.this.raidersListData.add(raidersListData);
                        raidersListAdapter.notifyItemInserted(RaidersActivity.this.raidersListData.size()-1);
                        if (RaidersActivity.this.raidersListData.size() == 2){
                            raidersListAdapter.notifyItemChanged(RaidersActivity.this.raidersListData.size()-2);
                        }
                        if (!raidersListData.getImg_cover().startsWith("http")) {
                            upImg(raidersListData.getImg_cover(),position);
                        }
                    }
                }).show(getSupportFragmentManager(),"add");
            }
        });
        raidersListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClick(View v, final int position) {
                new MyDialogRaidersListFragment().setRaidersData(raidersListData.get(position))
                        .setOnEnterListener(new MyDialogRaidersListFragment.OnEnterListener() {
                    @Override
                    public void onEnter(RaidersListData<List<String>> raidersListData, String Tag) {
                        raidersListAdapter.notifyItemChanged(position);
                        if (!raidersListData.getImg_cover().startsWith("http")) {
                            upImg(raidersListData.getImg_cover(), position);
                        }
                    }
                }).show(getSupportFragmentManager(),"add");
            }

            @Override
            public boolean OnLongClick(View v, int position) {
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_send:
                if (isEmtry()){
                    break;
                }
                setLoadDialog(true);
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("id",Constant.userData.getId())
                        .addFormDataPart("status",String.valueOf(status))
                        .addFormDataPart("title",et_title.getText().toString())
                        .addFormDataPart("cover",raidersListData.get(0).getImg_cover())
                        .addFormDataPart("raiders_type",new JSONArray(tagList).toString())
                        .addFormDataPart("introduction",et_introduction.getText().toString())
                        .addFormDataPart("raiders_content", new Gson().toJson(raidersListData));
                mPresenter.shareRaiders(builder.build().parts());
                break;
            case R.id.tv_power:
                if (status == 1){
                    tv_power.setText("私有");
                    status = 0;
                }else {
                    tv_power.setText("公开");
                    status = 1;
                }
                break;
        }
    }

    @Override
    public void onUpImgSuccess(BaseData<String> model, int position) {
        setLoadDialog(false);
        raidersListData.get(position).setImg_cover(model.getData());
        raidersListAdapter.notifyItemChanged(position);
    }

    @Override
    public void onUpImgFail(String msg, int position) {
        super.onFail(msg);
        if (!raidersListData.get(position).getImg_cover().startsWith("http")) {
            upImg(raidersListData.get(position).getImg_cover(), position);
        }
    }

    @Override
    public void onShareSuccess(String msg) {
        super.onSuccess(msg);
        finish();
    }

    @Override
    public void onShareFail(String msg) {
        super.onFail(msg);
    }

    /**
     * 压缩并上传图片
     * @param fileName
     * @param position
     */
    public void upImg(String fileName, final int position) {
        setLoadDialog(true);
        new Compressor(RaidersActivity.this)
                .compressToFileAsFlowable(new File(fileName))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        MultipartBody.Builder builder = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("id", Constant.userData.getId())
                                .addFormDataPart("path", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
                        mPresenter.upImg(builder.build().parts(), position);
                    }
                });
    }

    /**
     * 判断必填的是否已填
     * @return
     */
    public boolean isEmtry(){
        if (et_title.getText().toString().trim().isEmpty()){
            ToastUtils.showSingleToast("请输入攻略标题");
            return true;
        }else if (tagList.size() == 0){
            ToastUtils.showSingleToast("请添加攻略标签");
            return true;
        }else if (et_introduction.getText().toString().trim().isEmpty()){
            ToastUtils.showSingleToast("请输入攻略简介");
            return true;
        }else if (raidersListData.size() == 0){
            ToastUtils.showSingleToast("请添加攻略内容");
            return true;
        }
        return false;
    }
}
