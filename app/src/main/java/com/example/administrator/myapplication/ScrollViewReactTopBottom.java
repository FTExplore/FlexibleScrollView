package com.example.administrator.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2016/9/30.
 */
public class ScrollViewReactTopBottom extends ScrollView {

    private OnScrollTopBottomListener mListener;
    private boolean isIntercept = false;
    private int mCallCount;
    private int YOffset = 0;

    public interface OnScrollTopBottomListener {
        void srollToBottom(boolean isBottom);

        void scrollY(int YOffset);

        void scrollToTop(boolean isTop);
    }

    public void setScrollTopBottomListener(OnScrollTopBottomListener l) {
        mListener = l;
    }

    public ScrollViewReactTopBottom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        YOffset = getScrollY();
        //Log.e("ZHZ","Offset : " + getScrollY() + " : " + "t : " + t + " oldt " + oldt);
        if (oldt < t && t > 50) {
            if (overScrollListener != null)
                overScrollListener.OnScrollDown();
        }

        if (mListener != null) {
            mListener.scrollY(getScrollY());
        }

        if (getScrollY() == 0) {
            if (mListener != null) {
                mListener.scrollToTop(true);
            }
            return;
        } else {
            if (mListener != null) {
                mListener.scrollToTop(false);
            }
        }

        View view = this.getChildAt(0);
        if (this.getHeight() + this.getScrollY() == view.getHeight()) {
            mCallCount++;
            if (mCallCount == 1) {
                if (mListener != null) {
                    mListener.srollToBottom(true);
                }
            }
        } else {
            mCallCount = 0;
            if (mListener != null)
                mListener.srollToBottom(false);
        }
    }

    /**
     * 可判断方向的滑动监听接口
     */
    public interface OriScrollListener {
        void OnScrollUp();

        void OnScrollDown();

        void OverScrollUp(); // 滑动到顶部，继续向上滑动
    }

    private OriScrollListener overScrollListener;

    public void setOriScrollListener(OriScrollListener overScrollListener) {
        this.overScrollListener = overScrollListener;
    }

    private float startY;

    /*@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //startY = ev.getRawY();
                isIntercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                *//*float currY = ev.getRawY();
                if (currY - startY > 0 && YOffset == 0 && overScrollListener != null) {
                    overScrollListener.OverScrollUp();
                    break;
                }*//*

                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                break;
        }
        return super.onInterceptTouchEvent(ev);
    }*/

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getRawY();
                isIntercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float currY = ev.getRawY();
                if (currY - startY > 0 && YOffset == 0 && overScrollListener != null) {
                    overScrollListener.OverScrollUp();
                    break;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setIntercept(boolean intercept) {
        isIntercept = intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isIntercept || super.onTouchEvent(ev);
    }
}
