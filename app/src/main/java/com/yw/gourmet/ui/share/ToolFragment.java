package com.yw.gourmet.ui.share;

import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseFragment;
import com.yw.gourmet.listener.OnToolClickListener;

import static android.view.animation.AnimationUtils.loadAnimation;
import static com.yw.gourmet.ui.share.ToolType.CENTER;
import static com.yw.gourmet.ui.share.ToolType.LEFT;
import static com.yw.gourmet.ui.share.ToolType.RIGHT;

/**
 * Created by Lewis-v on 2017/12/18.
 */

public class ToolFragment extends BaseFragment implements View.OnClickListener{
    private static final String TAG = "ToolFragment";
    private LinearLayout ll_tool;
    private OnToolClickListener onToolClickListener;
    private ImageView img_choose,img_bold,img_italic,img_left,img_center,img_right;
    private ToolType type;
    private HorizontalScrollView scroll_tool;

    @Override
    protected void initView() {
        scroll_tool = view.findViewById(R.id.scroll_tool);

        ll_tool = (LinearLayout)view.findViewById(R.id.ll_tool);

        img_choose = (ImageView) view.findViewById(R.id.img_choose);
        img_bold = (ImageView) view.findViewById(R.id.img_bold);
        img_italic = (ImageView) view.findViewById(R.id.img_italic);
        img_left = (ImageView) view.findViewById(R.id.img_left);
        img_center = (ImageView) view.findViewById(R.id.img_center);
        img_right = (ImageView) view.findViewById(R.id.img_right);

        img_choose.setOnClickListener(this);
        img_bold.setOnClickListener(this);
        img_italic.setOnClickListener(this);
        img_left.setOnClickListener(this);
        img_center.setOnClickListener(this);
        img_right.setOnClickListener(this);

        if (type != null){
            if (type.isBold()){
                img_bold.setColorFilter(R.color.word_black, PorterDuff.Mode.SRC_IN);
            }
            if (type.isItalic()){
                img_italic.setColorFilter(R.color.word_black, PorterDuff.Mode.SRC_IN);
            }
            setTextType(type.getTextType());
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tool;
    }

    @Override
    public Animation onCreateAnimation(int transit, final boolean enter, int nextAnim) {
        Animation anim;
        if (enter){//进入动画
            anim = loadAnimation(getActivity(), R.anim.anim_view_enter_right);
        }else {//退出动画
            anim = loadAnimation(getActivity(), R.anim.anim_view_exit_right);
        }
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.e(TAG,"start"+(scroll_tool.getVisibility()==View.VISIBLE));
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.e(TAG,"end"+(scroll_tool.getVisibility()==View.VISIBLE));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.e(TAG,"repeat");
            }
        });
        return anim;
    }

    public ToolFragment setOnToolClickListener(OnToolClickListener onToolClickListener) {
        this.onToolClickListener = onToolClickListener;
        return this;
    }

    public ToolFragment setType(ToolType type) {
        this.type = type;
        return this;
    }

    @Override
    public void onClick(View view) {
        if (onToolClickListener != null && type != null) {
            switch (view.getId()) {
                case R.id.img_choose:
                    onToolClickListener.OnImgChoose();
                    break;
                case R.id.img_bold:
                    onToolClickListener.OnBold();
                    type.setBold(!type.isBold());
                    img_bold.clearColorFilter();//清除之前的滤镜
                    if (type.isBold()){
                        img_bold.setColorFilter(R.color.word_black, PorterDuff.Mode.SRC_IN);//显示上层滤镜
                    }else {
                        img_bold.setColorFilter(R.color.close, PorterDuff.Mode.DST);//显示下层图像
                    }
                    break;
                case R.id.img_italic:
                    onToolClickListener.OnItalic();
                    type.setItalic(!type.isItalic());
                    img_italic.clearColorFilter();
                    if (type.isItalic()){
                        img_italic.setColorFilter(R.color.word_black, PorterDuff.Mode.SRC_IN);
                    }else {
                        img_italic.setColorFilter(R.color.close, PorterDuff.Mode.DST);
                    }
                    break;
                case R.id.img_left:
                    onToolClickListener.OnTextLeft();
                    setTextType(LEFT);
                    break;
                case R.id.img_center:
                    onToolClickListener.OnTextCenter();
                    setTextType(CENTER);
                    break;
                case R.id.img_right:
                    onToolClickListener.OnTextRight();
                    setTextType(RIGHT);
                    break;
            }
        }
    }

    /**
     * 设置段落的样式
     * @param mode 模式,左对齐,居中,右对齐
     */
    public void setTextType(int mode){
        ImageView imageView = null;
        ImageView img_clear1 = null,img_clear2 = null;
        switch (mode){
            case LEFT:
                imageView = img_left;
                img_clear1 = img_center;
                img_clear2 = img_right;
                break;
            case CENTER:
                imageView = img_center;
                img_clear1 = img_left;
                img_clear2 = img_right;
                break;
            case RIGHT:
                imageView = img_right;
                img_clear1 = img_left;
                img_clear2 = img_center;
                break;
        }
        if (imageView != null && img_clear1 != null && img_clear2 != null) {
            type.setTextType(mode);
            imageView.clearColorFilter();
            imageView.setColorFilter(R.color.word_black, PorterDuff.Mode.SRC_IN);
            img_clear1.clearColorFilter();
            img_clear2.clearColorFilter();
            img_clear1.setColorFilter(R.color.close, PorterDuff.Mode.DST);
            img_clear2.setColorFilter(R.color.close, PorterDuff.Mode.DST);
        }
    }
}
