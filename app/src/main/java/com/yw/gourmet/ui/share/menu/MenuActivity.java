package com.yw.gourmet.ui.share.menu;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.MenuPracticeData;
import com.yw.gourmet.dialog.MyDialogIngredientFragment;
import com.yw.gourmet.dialog.MyDialogPhotoChooseFragment;
import com.yw.gourmet.listener.OnAddListener;
import com.yw.gourmet.listener.OnDeleteListener;
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
    private int difficultLevel = 1;//困难等级,默认为1
    private String coverPath;//封面地址
    private long create_time;//创建时间

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
        recycler_ingredient.setLayoutManager(new GridLayoutManager(this,2));
        adapterIngredient = new IngredientAdapter(this,listIngredient);
        recycler_ingredient.setAdapter(adapterIngredient);
        recycler_ingredient.setNestedScrollingEnabled(false);//禁止滑动
        adapterIngredient.notifyDataSetChanged();
        adapterIngredient.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClick(View v, int position) {
                String[] content = listIngredient.get(position).split(",");
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
                                                        .addFormDataPart("id",Constant.userData.getId())
                                                        .addFormDataPart("path",file.getName(),RequestBody.create(MediaType.parse("multipart/form-data"),file));
                                                mPresenter.upImg(builder.build().parts(),position);
                                            }
                                        });
                            }
                        }).show(getSupportFragmentManager(), "crop");
            }
        });
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
                finish();
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
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("id",Constant.userData.getId())
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
                                        .addFormDataPart("id", Constant.userData.getId())
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
        GlideApp.with(MenuActivity.this).load(model.getData()).error(R.mipmap.load_fail).into(img_cover);
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
}
