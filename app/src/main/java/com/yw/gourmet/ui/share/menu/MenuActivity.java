package com.yw.gourmet.ui.share.menu;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.IngredientAdapter;
import com.yw.gourmet.adapter.PracticeAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.dao.data.saveData.SaveData;
import com.yw.gourmet.dao.data.saveData.SaveDataUtil;
import com.yw.gourmet.dao.gen.SaveDataDao;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.MenuPracticeData;
import com.yw.gourmet.dialog.MyDialogIngredientFragment;
import com.yw.gourmet.dialog.MyDialogPhotoChooseFragment;
import com.yw.gourmet.dialog.MyDialogTipFragment;
import com.yw.gourmet.listener.OnAddListener;
import com.yw.gourmet.listener.OnCancelClickListener;
import com.yw.gourmet.listener.OnDeleteListener;
import com.yw.gourmet.listener.OnItemClickListener;
import com.yw.gourmet.rxbus.EventSticky;
import com.yw.gourmet.rxbus.RxBus;
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

public class MenuActivity extends BaseActivity<MenuPresenter> implements View.OnClickListener
        , MyDialogPhotoChooseFragment.OnCropListener,MenuContract.View,MyDialogIngredientFragment.OnEnterListener {
    public static final String[] levelText = {"非常简单","简单","一般","较困难","非常困难"};//难度等级的文字

    private ImageView img_cover;
    private TextView tv_power,tv_difficult,tv_cancel,tv_send;
    private EditText et_introduction,et_tip,et_title,et_time_hour,et_time_min;
    private RecyclerView recycler_ingredient,recycler_practice;
    private int status = 1;//权限,公开或私有,1公开,0私有,默认公开
    private IngredientAdapter adapterIngredient;//用料适配器
    private PracticeAdapter adapterPractice;//步骤适配器
    private List<String> listIngredient = new ArrayList<>();//用料列表
    private List<MenuPracticeData<List<String>>> listPractice = new ArrayList<>();//步骤列表
    private List<ImageView> difficultList = new ArrayList<>();//难度条
    private int difficultLevel = 0;//困难等级,默认为0
    private String coverPath;//封面地址
    private long create_time;//创建时间
    private SaveData saveData,saveDataCache;//本地数据库中的数据

    @Override
    protected int getLayoutId() {
        return R.layout.activity_menu;
    }

    @Override
    protected void initView() {
        view_parent = findViewById(R.id.view_parent);

        create_time = System.currentTimeMillis()/1000;
        toolbar = (Toolbar)findViewById(R.id.toolbar);

        tv_difficult = (TextView)findViewById(R.id.tv_difficult);
        tv_power = (TextView)findViewById(R.id.tv_power);
        tv_cancel = (TextView)findViewById(R.id.tv_cancel);
        tv_send = (TextView)findViewById(R.id.tv_send);
        tv_send.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        tv_power.setOnClickListener(this);

        ImageView imageView = (ImageView)findViewById(R.id.img_difficult1);
        imageView.setOnClickListener(this);
        difficultList.add(imageView);
        imageView = (ImageView)findViewById(R.id.img_difficult2);
        imageView.setOnClickListener(this);
        difficultList.add(imageView);
        imageView = (ImageView)findViewById(R.id.img_difficult3);
        imageView.setOnClickListener(this);
        difficultList.add(imageView);
        imageView = (ImageView)findViewById(R.id.img_difficult4);
        imageView.setOnClickListener(this);
        difficultList.add(imageView);
        imageView = (ImageView)findViewById(R.id.img_difficult5);
        imageView.setOnClickListener(this);
        difficultList.add(imageView);

        img_cover = (ImageView)findViewById(R.id.img_cover);
        img_cover.setOnClickListener(this);

        et_introduction = (EditText)findViewById(R.id.et_introduction);
        et_tip = (EditText)findViewById(R.id.et_tip);
        et_title = (EditText)findViewById(R.id.et_title);
        et_time_hour = (EditText)findViewById(R.id.et_time_hour);
        et_time_min = (EditText)findViewById(R.id.et_time_min);

        recycler_ingredient = (RecyclerView)findViewById(R.id.recycler_ingredient);
        recycler_ingredient.setItemAnimator(new DefaultItemAnimator());
        recycler_ingredient.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.HORIZONTAL));
        adapterIngredient = new IngredientAdapter(this,listIngredient);
        recycler_ingredient.setAdapter(adapterIngredient);
        recycler_ingredient.setNestedScrollingEnabled(false);//禁止滑动
        adapterIngredient.notifyDataSetChanged();
        adapterIngredient.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClick(View v, int position) {
                String[] content = listIngredient.get(position).split("&&");
                if (content.length > 1) {
                    new MyDialogIngredientFragment().setChange(adapterIngredient.isChange()).setPosition(position)
                            .setOnEnterListener(MenuActivity.this).setLeftText(content[0]).setRightText(content[1])
                            .show(getSupportFragmentManager(),"change");
                }
            }

            @Override
            public boolean OnLongClick(View v, int position) {
                return false;
            }
        });
        adapterIngredient.setOnAddListener(new OnAddListener() {
            @Override
            public void OnAdd(View view, int position) {
               new MyDialogIngredientFragment().setLeftHint("食材名称").setRightHint("用量")
                       .setOnEnterListener(MenuActivity.this).setPosition(position)
                       .show(getSupportFragmentManager(),"add");
            }
        });
        adapterIngredient.setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void OnDelete(View v, int position) {
                listIngredient.remove(position);
                adapterIngredient.notifyItemRemoved(position);
            }
        });


        recycler_practice = (RecyclerView)findViewById(R.id.recycler_practice);
        recycler_practice.setItemAnimator(new DefaultItemAnimator());
        recycler_practice.setLayoutManager(new LinearLayoutManager(this));
        recycler_practice.setNestedScrollingEnabled(false);
        adapterPractice = new PracticeAdapter(this,listPractice,getSupportFragmentManager(),true,5);
        recycler_practice.setAdapter(adapterPractice);
        adapterPractice.setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void OnDelete(View v, int position) {

            }
        });
        adapterPractice.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClick(View v, int position) {

            }

            @Override
            public boolean OnLongClick(View v, int position) {
                return false;
            }
        });
        adapterPractice.setOnAddListener(new OnAddListener() {
            @Override
            public void OnAdd(View view, int position) {
                View view1;
                if ((view1 = MenuActivity.this.getCurrentFocus()) != null){
                    view1.clearFocus();
                }
                MenuPracticeData<List<String>> menuPracticeData = new MenuPracticeData<>();
                menuPracticeData.setImg_practiceData(new ArrayList<String>());
                listPractice.add(menuPracticeData);
                adapterPractice.notifyItemInserted(listPractice.size() - 1);
            }
        });
        adapterPractice.setOnImgAddListener(new OnAddListener() {
            @Override
            public void OnAdd(View view, final int position) {
                new MyDialogPhotoChooseFragment().setCrop(true)
                        .setOnCropListener(new MyDialogPhotoChooseFragment.OnCropListener() {
                            @Override
                            public void OnCrop(String path, String tag) {
                                setLoadDialog(true);
                                new Compressor(MenuActivity.this)
                                        .compressToFileAsFlowable(new File(path))
                                        .observeOn(Schedulers.io())
                                        .subscribeOn(Schedulers.io())
                                        .subscribe(new Consumer<File>() {
                                            @Override
                                            public void accept(File file) throws Exception {
                                                MultipartBody.Builder builder = new MultipartBody.Builder()
                                                        .setType(MultipartBody.FORM)
                                                        .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                                                        .addFormDataPart("id",Constant.userData.getUser_id())
                                                        .addFormDataPart("path",file.getName(),RequestBody.create(MediaType.parse("multipart/form-data"),file));
                                                mPresenter.upImg(builder.build().parts(),position);
                                            }
                                        });
                            }
                        }).show(getSupportFragmentManager(), "crop");
            }
        });

        String type = getIntent().getStringExtra("type");
        if (type != null){
            List<SaveData> data = null;
            switch (type){
                case "new"://打开新的
                    data = SaveDataUtil
                            .querydataById(SaveDataDao.Properties.Type.eq(Constant.TypeFlag.MENU)
                                    ,SaveDataDao.Properties.User_id.eq(Constant.userData.getUser_id()));
                    if (data != null && data.size()>0) {
                        saveDataCache = data.get(0);
                        new MyDialogTipFragment().setTextEnter("是").setTextCancel("否")
                                .setShowText("草稿箱中存在未完成食谱,是否继续上次的编辑?")
                                .setOnEnterListener(new MyDialogTipFragment.OnEnterListener() {
                                    @Override
                                    public void OnEnter(String Tag) {
                                        saveData = saveDataCache;
                                        initSaveData(saveData);
                                    }
                                }).show(getSupportFragmentManager(), "tip");
                    }
                    break;
                case "change"://更改之前的
                    data = SaveDataUtil
                            .querydataById(SaveDataDao.Properties.Type.eq(Constant.TypeFlag.MENU)
                            ,SaveDataDao.Properties._id.eq(getIntent().getLongExtra("_id",0))
                                    ,SaveDataDao.Properties.User_id.eq(Constant.userData.getUser_id()));
                    if (data != null && data.size()>0){
                        saveData = data.get(0);
                    }
                    initSaveData(saveData);
                    break;
            }

        }
    }

    /**
     * 配置已存储的数据
     * @param saveData
     */
    public void initSaveData(SaveData saveData){
        if (saveData != null){
            et_title.setText(saveData.getTitle());
            status = saveData.getStatus();
            if (status == 1){
                tv_power.setText("公开");
            }else if (status == 0){
                tv_power.setText("私有");
            }
            GlideApp.with(this).load(saveData.getCover()).placeholder(R.mipmap.loading).into(img_cover);
            difficultLevel = saveData.getDifficult_level();
            setDifficultLevel(difficultLevel);
            String play = saveData.getPlay_time();
            if (play != null && play.length() > 0) {
                String[] playTime = play.split("&&");
                if (playTime.length == 2) {
                    et_time_hour.setText(playTime[0]);
                    et_time_min.setText(playTime[1]);
                }
            }
            et_introduction.setText(saveData.getIntroduction());
            et_tip.setText(saveData.getTip());
            listIngredient.clear();
            listIngredient.addAll(saveData.getIngredient());
            Log.e("---ingre---",saveData.getIngredient().toString());
            adapterIngredient.notifyDataSetChanged();
            listPractice.clear();
            listPractice.addAll(saveData.getPractice());
            adapterPractice.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_cover:
                if (Constant.userData != null) {
                    new MyDialogPhotoChooseFragment().setChooseNum(1).setRatio(1).setCrop(true)
                            .setOnCropListener(this).show(getSupportFragmentManager(), "cover");
                }else {
                    ToastUtils.showLongToast("请登录后在进行操作");
                }
                break;
            case R.id.tv_power:
                if (status == 1){
                    status = 0;
                    tv_power.setText("私有");
                }else {
                    status = 1;
                    tv_power.setText("公开");
                }
                break;
            case R.id.tv_cancel:
                back();
                break;
            case R.id.img_difficult1:
                setDifficultLevel(0);
                break;
            case R.id.img_difficult2:
                setDifficultLevel(1);
                break;
            case R.id.img_difficult3:
                setDifficultLevel(2);
                break;
            case R.id.img_difficult4:
                setDifficultLevel(3);
                break;
            case R.id.img_difficult5:
                setDifficultLevel(4);
                break;
            case R.id.tv_send:
                if (isEmpty()){
                    break;
                }
                new MyDialogTipFragment().setShowText("是否分享您的食谱")
                        .setOnEnterListener(new MyDialogTipFragment.OnEnterListener() {
                            @Override
                            public void OnEnter(String Tag) {
                                MultipartBody.Builder builder = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                                        .addFormDataPart("id",Constant.userData.getUser_id())
                                        .addFormDataPart("status",String.valueOf(status))
                                        .addFormDataPart("title",et_title.getText().toString())
                                        .addFormDataPart("cover",coverPath)
                                        .addFormDataPart("difficult_level",String.valueOf(difficultLevel))
                                        .addFormDataPart("play_time",et_time_hour.getText().toString()+","+et_time_min.getText().toString())
                                        .addFormDataPart("introduction",et_introduction.getText().toString())
                                        .addFormDataPart("practice",listPractice.toString())
                                        .addFormDataPart("create_time",String.valueOf(create_time))
                                        .addFormDataPart("ingredient",new JSONArray(listIngredient).toString());
                                if (!et_tip.getText().toString().trim().isEmpty()){
                                    builder.addFormDataPart("tip",et_tip.getText().toString());
                                }
                                mPresenter.putMenu(builder.build().parts());
                            }
                        }).show(getSupportFragmentManager(),"share");

                break;
        }
    }

    @Override
    public void OnCrop(String path, String tag) {
        setLoadDialog(true);
        new Compressor(this)
                .compressToFileAsFlowable(new File(path))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(final File file) throws Exception {
                        img_cover.post(new Runnable() {
                            @Override
                            public void run() {
                                MultipartBody.Builder builder =new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                                        .addFormDataPart("id", Constant.userData.getUser_id())
                                        .addFormDataPart("path",file.getName()
                                                , RequestBody.create(MediaType.parse("multipart/form-data"),file));
                                mPresenter.upImg(builder.build().parts());
                            }
                        });
                    }
                });
    }

    @Override
    public void onUpImgSuccess(BaseData<String> model) {
        setLoadDialog(false);
        GlideApp.with(MenuActivity.this).load(model.getData()).error(R.mipmap.load_fail)
                .placeholder(R.mipmap.loading).into(img_cover);
        coverPath = model.getData();
    }

    @Override
    public void onUpImgSuccess(BaseData<String> model, int position) {
        setLoadDialog(false);
        adapterPractice.getAddAdapterList().get(position).addImg(model.getData());
    }

    @Override
    public void onPutMenuSuccess(BaseData model) {
        super.onSuccess(model.getMessage());
        try {
            if (saveData != null) {
                SaveDataUtil.delete(saveData.get_id());
            }
        }catch (Exception e){}
        RxBus.getDefault().postSticky(new EventSticky("gourmet_refresh"));
        finish();
    }

    @Override
    public void OnEnter(String edit,int position, String tag) {
        if (tag.equals("add")){
            listIngredient.add(edit);
            adapterIngredient.notifyItemInserted(position);
        }else if (tag.equals("change")){
            listIngredient.set(position,edit);
            adapterIngredient.notifyItemChanged(position);
        }
    }

    /**
     * 设置难度等级的显示
     * @param level
     */
    public synchronized void setDifficultLevel(int level) {
        if (level >= 0 && level <= 4){
            tv_difficult.setText(levelText[level]);
        }
        for (int i = 0 ,len = difficultList.size(); i<len ; i++ ){
            if (i <= level) {
                difficultList.get(i).setImageResource(R.drawable.difficult_back_accent);
            }else {
                difficultList.get(i).setImageResource(R.drawable.ingredient_back);
            }
        }
        difficultLevel = level;
    }

    /**
     * 发布前判断是否有必填数据为空
     * @return
     */
    public boolean isEmpty(){
        if (et_title.getText().toString().trim().isEmpty()){
            et_title.requestFocus();
            ToastUtils.showLongToast("请输入标题");
            return true;
        }
        if (coverPath == null){
            ToastUtils.showLongToast("请选择食谱的封面");
            return true;
        }
        if (et_time_hour.getText().toString().trim().isEmpty()){//耗时为空时补0
            et_time_hour.setText("0");
        }
        if (et_time_min.getText().toString().trim().isEmpty()){
            et_time_min.setText("0");
        }
        if (et_time_min.getText().toString().equals("0") && et_time_hour.getText().toString().equals("0")){//耗时两个都为0
            ToastUtils.showLongToast("请输入制作耗时");
            return true;
        }
        if (et_introduction.getText().toString().trim().isEmpty()){
            et_introduction.requestFocus();
            ToastUtils.showLongToast("请输入食谱简介");
            return true;
        }
        if (adapterPractice.isEmpty()){
            ToastUtils.showLongToast("请输入食谱步骤");
            return true;
        }
        if (adapterIngredient.isEmpty()){
            ToastUtils.showLongToast("请输入食谱用料");
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
        }else if (coverPath!=null && !coverPath.trim().isEmpty()){
            return true;
        }else if (!et_introduction.getText().toString().trim().isEmpty()){
            return true;
        }else if (listIngredient != null && listIngredient.size()>0){
            return true;
        }else if (listPractice != null && listPractice.size()>0){
            return true;
        }else if (!et_tip.getText().toString().trim().isEmpty()){
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
                                    .setCover(coverPath).setDifficult_level(difficultLevel)
                                    .setPlay_time(et_time_hour.getText().toString()+"&&"+et_time_min.getText().toString())
                                    .setIntroduction(et_introduction.getText().toString())
                                    .setTip(et_tip.getText().toString())
                                    .setIngredient(listIngredient).setPractice(listPractice).setType(Constant.TypeFlag.MENU);
                            if (MenuActivity.this.saveData == null) {
                                saveData.set_id(System.currentTimeMillis());
                                SaveDataUtil.insert(saveData);
                            }else {
                                saveData.set_id(MenuActivity.this.saveData.get_id());
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
