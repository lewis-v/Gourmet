package com.yw.gourmet.ui.share.menu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yw.gourmet.Constant;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.IngredientAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.dialog.MyDialogIngredientFragment;
import com.yw.gourmet.dialog.MyDialogPhotoChooseFragment;
import com.yw.gourmet.listener.OnAddListener;
import com.yw.gourmet.listener.OnDeleteListener;
import com.yw.gourmet.listener.OnEditDialogEnterClickListener;
import com.yw.gourmet.listener.OnItemClickListener;

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
    private ImageView img_cover;
    private TextView tv_power,tv_difficult,tv_cancel;
    private EditText et_introduction,et_tip,et_title,et_time_hour,et_time_min;
    private RecyclerView recycler_ingredient,recycler_practice;
    private int status = 1;//权限,公开或私有,1公开,0私有,默认公开
    private IngredientAdapter adapterIngredient;//用料适配器
    private List<String> listIngredient = new ArrayList<>();
    private List<ImageView> difficultList = new ArrayList<>();//难度条
    private int difficultLevel = 1;//困难等级,默认为1
    private final String[] levelText = {"非常简单","简单","一般","较困难","非常困难"};//难度等级的文字

    @Override
    protected int getLayoutId() {
        return R.layout.activity_menu;
    }

    @Override
    protected void initView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);

        tv_difficult = (TextView)findViewById(R.id.tv_difficult);
        tv_power = (TextView)findViewById(R.id.tv_power);
        tv_cancel = (TextView)findViewById(R.id.tv_cancel);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_cover:
                new MyDialogPhotoChooseFragment().setChooseNum(1).setRatio(1).setCrop(true)
                        .setOnCropListener(this).show(getSupportFragmentManager(),"cover");
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
}
