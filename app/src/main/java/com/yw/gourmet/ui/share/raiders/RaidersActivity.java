package com.yw.gourmet.ui.share.raiders;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.IngredientAdapter;
import com.yw.gourmet.adapter.RaidersListAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.dao.data.saveData.SaveData;
import com.yw.gourmet.dao.data.saveData.SaveDataUtil;
import com.yw.gourmet.dao.gen.SaveDataDao;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.RaidersListData;
import com.yw.gourmet.dialog.MyDialogEditFragment;
import com.yw.gourmet.dialog.MyDialogRaidersListFragment;
import com.yw.gourmet.dialog.MyDialogTipFragment;
import com.yw.gourmet.listener.OnAddListener;
import com.yw.gourmet.listener.OnCancelClickListener;
import com.yw.gourmet.listener.OnDeleteListener;
import com.yw.gourmet.listener.OnEditDialogEnterClickListener;
import com.yw.gourmet.listener.OnItemClickListener;
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
    private SaveData saveData,saveDataCache;

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

        String type = getIntent().getStringExtra("type");
        if (type != null){
            List<SaveData> data ;
            switch (type){
                case "new":
                    data = SaveDataUtil
                            .querydataById(SaveDataDao.Properties.Type.eq(Constant.TypeFlag.RAIDERS)
                                    , SaveDataDao.Properties.User_id.eq(Constant.userData.getUser_id()));
                    if (data != null && data.size()>0) {
                        saveDataCache = data.get(0);
                        new MyDialogTipFragment().setTextEnter("是").setTextCancel("否")
                                .setShowText("草稿箱中存在未完成攻略,是否继续上次的编辑?")
                                .setOnEnterListener(new MyDialogTipFragment.OnEnterListener() {
                                    @Override
                                    public void OnEnter(String Tag) {
                                        saveData = saveDataCache;
                                        initSaveData(saveData);
                                    }
                                }).show(getSupportFragmentManager(), "tip");
                    }
                    break;
                case "change":
                    data = SaveDataUtil
                            .querydataById(SaveDataDao.Properties.Type.eq(Constant.TypeFlag.RAIDERS)
                                    ,SaveDataDao.Properties._id.eq(getIntent().getLongExtra("_id",0))
                                    ,SaveDataDao.Properties.User_id.eq(Constant.userData.getUser_id()));
                    if (data != null && data.size()>0) {
                        saveData = data.get(0);
                        initSaveData(saveData);
                    }
                    break;
            }
        }
    }

    /**
     * 配置存储的数据
     * @param saveData
     */
    public void initSaveData(SaveData saveData){
        if (saveData != null){
            et_title.setText(saveData.getTitle());
            et_introduction.setText(saveData.getIntroduction());
            status = saveData.getStatus();
            if (status == 1){
                tv_power.setText("公开");
            }else if (status == 0){
                tv_power.setText("私有");
            }
            tagList.clear();
            tagList.addAll(saveData.getRaiders_type());
            tagAdapter.notifyDataSetChanged();
            raidersListData.clear();
            raidersListData.addAll(saveData.getRaiders_content());
            raidersListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                back();
                break;
            case R.id.tv_send:
                if (isEmtry()){
                    break;
                }
                new MyDialogTipFragment().setShowText("是否分享您的日记")
                        .setOnEnterListener(new MyDialogTipFragment.OnEnterListener() {
                            @Override
                            public void OnEnter(String Tag) {
                                setLoadDialog(true);
                                MultipartBody.Builder builder = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                                        .addFormDataPart("id",Constant.userData.getUser_id())
                                        .addFormDataPart("status",String.valueOf(status))
                                        .addFormDataPart("title",et_title.getText().toString())
                                        .addFormDataPart("cover",raidersListData.get(0).getImg_cover())
                                        .addFormDataPart("raiders_type",new JSONArray(tagList).toString())
                                        .addFormDataPart("introduction",et_introduction.getText().toString())
                                        .addFormDataPart("raiders_content", new Gson().toJson(raidersListData));
                                mPresenter.shareRaiders(builder.build().parts());
                            }
                        }).show(getSupportFragmentManager(),"share");
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
        try {
            if (saveData != null) {
                SaveDataUtil.delete(saveData.get_id());
            }
        }catch (Exception e){}
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
                                .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                                .addFormDataPart("id", Constant.userData.getUser_id())
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


    /**
     * 判断是否需要存储
     * @return
     */
    public boolean isNeedSave(){
        if (!et_title.getText().toString().trim().isEmpty()){
            return true;
        }else if (tagList!=null && tagList.size()>0){
            return true;
        }else if (!et_introduction.getText().toString().trim().isEmpty()){
            return true;
        }else if (raidersListData != null && raidersListData.size()>0){
            return true;
        }
        return false;
    }

    /**
     * 退出操作
     */
    public void back(){
        if (isNeedSave()){
            new MyDialogTipFragment().setShowText("是否将此次编辑内容保存到草稿箱?")
                    .setTextCancel("否")
                    .setTextEnter("是")
                    .setOnCancelClickListener(new OnCancelClickListener() {
                        @Override
                        public void OnCancel(String tag) {
                            finish();
                        }
                    })
                    .setOnEnterListener(new MyDialogTipFragment.OnEnterListener() {
                        @Override
                        public void OnEnter(String Tag) {
                            SaveData saveData = new SaveData();
                            saveData.setUser_id(Constant.userData.getUser_id())
                                    .setChange_time(System.currentTimeMillis())
                                    .setTitle(et_title.getText().toString()).setStatus(status)
                                    .setIntroduction(et_introduction.getText().toString())
                                    .setType(Constant.TypeFlag.RAIDERS)
                                    .setRaiders_type(tagList)
                                    .setRaiders_content(raidersListData);
                            if (RaidersActivity.this.saveData == null) {
                                saveData.set_id(System.currentTimeMillis());
                                SaveDataUtil.insert(saveData);
                            }else {
                                saveData.set_id(RaidersActivity.this.saveData.get_id());
                                SaveDataUtil.updata(saveData);
                            }
                            finish();
                        }
                    })
                    .show(getSupportFragmentManager(),"isSave");
        }else {
            finish();
        }
    }
}
