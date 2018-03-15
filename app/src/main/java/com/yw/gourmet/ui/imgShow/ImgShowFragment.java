package com.yw.gourmet.ui.imgShow;

import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.MyImgViewPagerAdapter;
import com.yw.gourmet.base.BaseFragment;
import com.yw.gourmet.widget.MyViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * auth: lewis-v
 * time: 2018/3/14.
 */

public class ImgShowFragment extends BaseFragment {
    private MyViewPager viewpager_photo;
    private MyImgViewPagerAdapter<PhotoView> adapter;
    private LinearLayout ll_dialog;
    private List<String> imgString = new ArrayList<>(2);
    private List<PhotoView> list = new ArrayList<>(2);
    private int position = 0;//默认显示位置,0
    private String shareFlag;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_dialog_photo_show;
    }

    @Override
    protected void initView() {
        postponeEnterTransition();
        viewpager_photo = (MyViewPager)view.findViewById(R.id.viewpager_photo);
        adapter = new MyImgViewPagerAdapter<PhotoView>(list);
        viewpager_photo.setAdapter(adapter);
        viewpager_photo.setPagingEnabled(true);
        setData();
        if (position < list.size()){
            viewpager_photo.setCurrentItem(position);
        }
    }


    public void setData(){
        if (imgString != null && imgString.size()>0){
            for (int len = imgString.size(),num = 0;num<len;num++){
                final PhotoView photoView = new PhotoView(getContext());
                final int i = num;
                if (position == num) {
                    photoView.setTransitionName(shareFlag);
                    startPostponedEnterTransition();
                }
                GlideApp.with(this).asBitmap().load(imgString.get(num)).error(R.mipmap.load_fail)
                        .placeholder(R.mipmap.loading).into(photoView);
                photoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                list.add(photoView);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void dismiss(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.remove(this);
        ft.commit();
    }

    public ImgShowFragment setImgString(List<String> imgString) {
        this.imgString = imgString;
        return this;
    }

    public ImgShowFragment setList(List<PhotoView> list) {
        this.list = list;
        return this;
    }

    public ImgShowFragment setPosition(int position) {
        this.position = position;
        return this;
    }

    public ImgShowFragment setShareFlag(String shareFlag) {
        this.shareFlag = shareFlag;
        return this;
    }
}
