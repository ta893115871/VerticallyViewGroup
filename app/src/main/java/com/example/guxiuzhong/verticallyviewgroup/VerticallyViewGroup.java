package com.example.guxiuzhong.verticallyviewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * @author 顾修忠-guxiuzhong@youku.com/gfj19900401@163.com
 * @Title: MyViewGroup
 * @Package com.example.guxiuzhong.viewgroup_01
 * @Description: 自定义View之自定义ViewGroup-01
 * @date 15/12/20
 * @time 下午12:57
 */
public class VerticallyViewGroup extends ViewGroup {

    public static final int MAX_DISTANCE = 200;
    private int mScreenHeight;
    private Scroller mScroller;
    private int mLastY, mStart, mEnd;


    public VerticallyViewGroup(Context context) {
        this(context, null);
    }

    public VerticallyViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticallyViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        mScroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        //设置ViewGroup的高度
        MarginLayoutParams mlp = (MarginLayoutParams) getLayoutParams();
        mlp.height = mScreenHeight * count;
        setLayoutParams(mlp);
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() != View.GONE) {
                childView.layout(l, i * mScreenHeight, r, (i + 1) * mScreenHeight);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                mStart = getScrollY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                int dy = mLastY - y;
                //第一页,向上拉,最高200PX,再会弹回来
                if (getScrollY() < -MAX_DISTANCE) {
                    dy = 0;
                }
//                System.out.println("------getHeight()()-->>>" + getHeight());
//                System.out.println("-----bbbbbb->>>>" + getBottom());
                if (getScrollY() > (getChildCount() - 1) * mScreenHeight) {
                    dy = 0;
                    System.out.println("------1111-->>>" + getHeight());
                }
                scrollBy(0, dy);
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                mEnd = getScrollY();
                int dScrollY = mEnd - mStart;
                if (dScrollY > 0) {//向下操作的
                    if (dScrollY < mScreenHeight / 3) {
                        mScroller.startScroll(0, getScrollY(), 0, -dScrollY);
                    } else {
                        mScroller.startScroll(0, getScrollY(), 0, mScreenHeight - dScrollY);
                    }
                } else {//向上操作的
                    if (-dScrollY < mScreenHeight / 3) {
                        mScroller.startScroll(0, getScrollY(), 0, -dScrollY);
                    } else {
                        mScroller.startScroll(0, getScrollY(), 0, -mScreenHeight - dScrollY);
                    }
                }
                break;
            default:
                break;
        }
        postInvalidate();
        return true;//不用交给上级了,我自己把活干了,小宝贝,嘻嘻
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //判断Scroller是否执行完毕
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            //通过重绘不断调用computeScroll
            postInvalidate();
        }
    }
}
