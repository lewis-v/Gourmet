package com.yw.gourmet.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * auth: lewis-v
 * time: 2018/1/7.
 */

public class CustomLayoutManager extends RecyclerView.LayoutManager {
    private int mVerticalOffset;//竖直偏移量 每次换行时，要根据这个offset判断
    private int mFirstVisiPos;//屏幕可见的第一个View的Position
    private int mLastVisiPos;//屏幕可见的最后一个View的Position

    private SparseArray<Rect> mItemRects;//key 是View的position，保存View的bounds 和 显示标志，

/*    private class FlowItem {
        public Rect bounds;//View的边界
        public boolean isShow;//View 是否显示
        public FlowItem(Rect bounds, boolean isShow) {
            this.bounds = bounds;
            this.isShow = isShow;
        }
    }*/

    public CustomLayoutManager() {
        setAutoMeasureEnabled(true);
        mItemRects = new SparseArray<>();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {//每次移动都会触发
        if (getItemCount() == 0) {//没有Item，界面空着吧
            detachAndScrapAttachedViews(recycler);
            return;
        }
        if (getChildCount() == 0 && state.isPreLayout()) {//state.isPreLayout()是支持动画的
            return;
        }
        //onLayoutChildren方法在RecyclerView 初始化时 会执行两遍
        detachAndScrapAttachedViews(recycler);

        //初始化区域
        mVerticalOffset = 0;
        mFirstVisiPos = 0;
        mLastVisiPos = getItemCount();

        //初始化时调用 填充childView
        fill(recycler, state);


    }

    /**
     * 初始化时调用 填充childView
     *
     * @param recycler
     * @param state
     */
    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        fill(recycler, state, 0);
    }

    /**
     * 填充childView的核心方法,应该先填充，再移动。
     * 在填充时，预先计算dy的在内，如果View越界，回收掉。
     * 一般情况是返回dy，如果出现View数量不足，则返回修正后的dy.
     *
     * @param recycler
     * @param state
     * @param dy       RecyclerView给我们的位移量,+,显示底端(向上滑)， -，显示头部(向下滑)
     * @return 修正以后真正的dy（可能剩余空间不够移动那么多了 所以return <|dy|）
     */
    private int fill(RecyclerView.Recycler recycler, RecyclerView.State state, int dy) {

        int topOffset = getPaddingTop();

        //回收越界子View
        if (getChildCount() > 0) {//初始化后,没滑动的情况下,返回的值为0,在滑动第一次后,之后返回的数值为实际显示的子视图数量
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View child = getChildAt(i);//获取可见视图的View
                if (dy > 0) {//需要回收当前屏幕，上越界的View
                    if (getDecoratedBottom(child) - dy < topOffset) {//getDecoratedBottom返回子视图底边缘在父视图的位置
                        removeAndRecycleView(child, recycler);
                        mFirstVisiPos++;
                        continue;
                    }
                } else if (dy < 0) {//回收当前屏幕，下越界的View
                    if (getDecoratedTop(child) - dy > getHeight() - getPaddingBottom()) {//此处dy为负数,即子视图的顶部边缘在滑动dy距离后所在的位置是否在父视图的显示区域中
                        removeAndRecycleView(child, recycler);
                        mLastVisiPos--;
                        continue;
                    }
                }
            }
            //detachAndScrapAttachedViews(recycler);
        }

        int leftOffset = getPaddingLeft();
        int lineMaxHeight = 0;
        //布局子View阶段
        if (dy >= 0) {//上滑,上越界
            int minPos = mFirstVisiPos;
            mLastVisiPos = getItemCount() - 1;//此处变为总item数量
            if (getChildCount() > 0) {//回收后还存在可视的视图
                View lastView = getChildAt(getChildCount() - 1);//回收后可视view的最后一个view
                minPos = getPosition(lastView) + 1;//从最后一个View+1开始吧(可视视图最后一个在所有视图中的位置(包括已经回收的))
                topOffset = getDecoratedTop(lastView);
                leftOffset = getDecoratedRight(lastView);
                lineMaxHeight = Math.max(lineMaxHeight, getDecoratedMeasurementVertical(lastView));
            }
            //顺序addChildView
            for (int i = minPos; i <= mLastVisiPos; i++) {
                //找recycler要一个childItemView,我们不管它是从scrap里取，还是从RecyclerViewPool里取，亦或是onCreateViewHolder里拿。
                View child = recycler.getViewForPosition(i);
                addView(child);//添加到recycle中
                measureChildWithMargins(child, 0, 0);//让recycle为child分配尺寸
                //计算宽度 包括margin
                if (leftOffset + getDecoratedMeasurementHorizontal(child) <= getHorizontalSpace()) {//当前行还排列的下
                    layoutDecoratedWithMargins(child, leftOffset, topOffset, leftOffset
                            + getDecoratedMeasurementHorizontal(child), topOffset + getDecoratedMeasurementVertical(child));//让recycle为child设置边距(坐标值)

                    //保存Rect供逆序layout用
                    Rect rect = new Rect(leftOffset, topOffset + mVerticalOffset, leftOffset
                            + getDecoratedMeasurementHorizontal(child), topOffset + getDecoratedMeasurementVertical(child) + mVerticalOffset);//保存子视图在总视图的坐标位置
                    mItemRects.put(i, rect);

                    //改变 left  lineHeight
                    leftOffset += getDecoratedMeasurementHorizontal(child);
                    lineMaxHeight = Math.max(lineMaxHeight, getDecoratedMeasurementVertical(child));
                } else {//当前行排列不下
                    //改变top  left  lineHeight
                    leftOffset = getPaddingLeft();
                    topOffset += lineMaxHeight;
                    lineMaxHeight = 0;

                    //新起一行的时候要判断一下边界
                    if (topOffset - dy > getHeight() - getPaddingBottom()) {
                        //越界了 就回收
                        removeAndRecycleView(child, recycler);
                        mLastVisiPos = i - 1;//设置此位置为添加view的最后一个,跳出添加的循环
                    } else {
                        layoutDecoratedWithMargins(child, leftOffset, topOffset, leftOffset + getDecoratedMeasurementHorizontal(child), topOffset + getDecoratedMeasurementVertical(child));

                        //保存Rect供逆序layout用
                        Rect rect = new Rect(leftOffset, topOffset + mVerticalOffset, leftOffset + getDecoratedMeasurementHorizontal(child), topOffset + getDecoratedMeasurementVertical(child) + mVerticalOffset);
                        mItemRects.put(i, rect);

                        //改变 left  lineHeight
                        leftOffset += getDecoratedMeasurementHorizontal(child);
                        lineMaxHeight = Math.max(lineMaxHeight, getDecoratedMeasurementVertical(child));
                    }
                }
            }
            //添加完后，判断是否已经没有更多的ItemView，并且此时屏幕仍有空白，则需要修正dy
            View lastChild = getChildAt(getChildCount() - 1);
            if (getPosition(lastChild) == getItemCount() - 1) {//为最后一个
                int gap = getHeight() - getPaddingBottom() - getDecoratedBottom(lastChild);//总视图的底部可能回有一部分的空间多出来,获取此高度并返回此值,以修正底部
                if (gap > 0) {
                    dy -= gap;
                }

            }

        } else {//向下滑,下越界
            /**
             * ##  利用Rect保存子View边界
             正序排列时，保存每个子View的Rect，逆序时，直接拿出来layout。
             */
            int maxPos = getItemCount() - 1;
            mFirstVisiPos = 0;
            if (getChildCount() > 0) {//回收后还存在可视的视图
                View firstView = getChildAt(0);//获取第一个可视视图
                maxPos = getPosition(firstView) - 1;//可视视图的第一个即为最后一个
            }
            for (int i = maxPos; i >= mFirstVisiPos; i--) {
                Rect rect = mItemRects.get(i);

                if (rect.bottom - mVerticalOffset - dy < getPaddingTop()) {
                    mFirstVisiPos = i + 1;
                    break;
                } else {
                    View child = recycler.getViewForPosition(i);
                    addView(child, 0);//将View添加至RecyclerView中，childIndex为1，但是View的位置还是由layout的位置决定
                    measureChildWithMargins(child, 0, 0);

                    layoutDecoratedWithMargins(child, rect.left, rect.top - mVerticalOffset, rect.right, rect.bottom - mVerticalOffset);
                }
            }
        }


        Log.d("TAG", "count= [" + getChildCount() + "]" + ",[recycler.getScrapList().size():" + recycler.getScrapList().size() + ", dy:" + dy + ",  mVerticalOffset" + mVerticalOffset + ", ");

        return dy;
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        //位移0、没有子View 当然不移动
        if (dy == 0 || getChildCount() == 0) {
            return 0;
        }

        int realOffset = dy;//实际滑动的距离， 可能会在边界处被修复
        //边界修复代码
        if (mVerticalOffset + realOffset < 0) {//上边界
            realOffset = -mVerticalOffset;
        } else if (realOffset > 0) {//下边界
            //利用最后一个子View比较修正
            View lastChild = getChildAt(getChildCount() - 1);
            if (getPosition(lastChild) == getItemCount() - 1) {
                int gap = getHeight() - getPaddingBottom() - getDecoratedBottom(lastChild);
                if (gap > 0) {
                    realOffset = -gap;
                } else if (gap == 0) {
                    realOffset = 0;
                } else {
                    realOffset = Math.min(realOffset, -gap);
                }
            }
        }

        realOffset = fill(recycler, state, realOffset);//先填充，再位移。

        mVerticalOffset += realOffset;//累加实际滑动距离

        offsetChildrenVertical(-realOffset);//滑动

        return realOffset;
    }

    //模仿LLM Horizontal 源码

    /**
     * 获取某个childView在水平方向所占的空间
     *
     * @param view
     * @return
     */
    public int getDecoratedMeasurementHorizontal(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + params.leftMargin
                + params.rightMargin;
    }

    /**
     * 获取某个childView在竖直方向所占的空间
     *
     * @param view
     * @return
     */
    public int getDecoratedMeasurementVertical(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + params.topMargin
                + params.bottomMargin;
    }

    /**
     * 获取recycle实际可视高度
     * @return
     */
    public int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    /**
     * 获取recycle实际可视宽度
     * @return
     */
    public int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }
}

