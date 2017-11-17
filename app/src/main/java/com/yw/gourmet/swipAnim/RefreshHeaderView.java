package com.yw.gourmet.swipAnim;

import android.content.Context;
import android.util.AttributeSet;

import com.aspsine.swipetoloadlayout.SwipeRefreshTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;

/**
 * Created by lyw on 2017-08-07.
 */

public class RefreshHeaderView extends android.support.v7.widget.AppCompatTextView implements SwipeRefreshTrigger, SwipeTrigger {

    public RefreshHeaderView(Context context) {
        super(context);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    public void onRefresh() {//刷新中
    }

    @Override
    public void onPrepare() {//拉动时触发1(只要拉动,哪怕是一点点也触发)
    }

    @Override
    public void onMove(int yScrolled, boolean isComplete, boolean automatic) {//移动是触发2
        if (!isComplete) {
            if (yScrolled >= getHeight()) {
                setText("释放更新");
            } else {
                setText("下拉刷新");
            }
        } else {
            setText("");//刷新结束后回弹
        }
    }

    @Override
    public void onRelease() {//拉到一定程度释放时触发(拉到可以更新的距离)
    }

    @Override
    public void onComplete() {//加载完成触发
        setText("加载完成");
    }

    @Override
    public void onReset() {//恢复原始状态时触发
    }
}

