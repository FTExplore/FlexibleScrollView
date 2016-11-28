package com.example.administrator.myapplication;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * 自动伸缩高度的控件,目前只考虑与com.yunfan.filmtalent.UI.Views.ScrollView.ScrollViewReactTopBottom
 * 配合使用，日后发展其兼容性
 * Created by zhanghongzuo on 2016/11/14.
 */

public class FlexLinearLayout extends LinearLayout {
    private ScrollViewReactTopBottom mScrollView;

    public void setTitle(TextView title) {
        this.title = title;
    }

    private TextView title;
    private int duration = 180; // 伸缩动画时长
    private int shrinkedHeight; // 缩后的高度
    private boolean isExpand = true;
    private ShrinkAnim mShrinkAnim = new ShrinkAnim();
    private ExpandAnim mExpandAnim = new ExpandAnim();
    private OnMeasuredListener mMeasuredListener;
    private ValueAnimator colorAnimShrink;
    private ValueAnimator colorAnimExpand;

    public void colorAnimBindView(View linkageView) {
        colorAnimShrink = ObjectAnimator.ofInt(linkageView,"backgroundColor", getResources().getColor(R.color.transparent), Color.parseColor("#E6000000"));
        colorAnimShrink.setDuration(duration);
        colorAnimShrink.setEvaluator(new ArgbEvaluator());
        colorAnimShrink.setRepeatCount(0);
        colorAnimShrink.setRepeatMode(ValueAnimator.REVERSE);
        //
        colorAnimExpand = ObjectAnimator.ofInt(linkageView,"backgroundColor", Color.parseColor("#E6000000"), getResources().getColor(R.color.transparent));
        colorAnimExpand.setDuration(duration);
        colorAnimExpand.setEvaluator(new ArgbEvaluator());
        colorAnimExpand.setRepeatCount(0);
        colorAnimExpand.setRepeatMode(ValueAnimator.REVERSE);
    }

    public void setMeasuredListener(OnMeasuredListener mMeasuredListener) {
        this.mMeasuredListener = mMeasuredListener;
    }

    public interface OnMeasuredListener {
        void getMeasuredHeight(int height);
    }

    public FlexLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public FlexLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FlexLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setScrollView(ScrollViewReactTopBottom mScrollView) {
        this.mScrollView = mScrollView;
    }

    public void setDuration(int duration) {
        this.duration = duration;
        mShrinkAnim.setDuration(duration);
        mExpandAnim.setDuration(duration);
        colorAnimShrink.setDuration(duration);
        colorAnimExpand.setDuration(duration);
    }

    public void setShrinkedHeight(int shrinkedHeight) {
        this.shrinkedHeight = shrinkedHeight;
    }

    private void init(Context ctx) {
        mShrinkAnim.setDuration(duration);
        mExpandAnim.setDuration(duration);
        mShrinkAnim.setAnimationListener(mShrinkAnimListener);
        mExpandAnim.setAnimationListener(mExpandAnimListener);
        mShrinkAnim.setInterpolator(new DecelerateInterpolator());
        mExpandAnim.setInterpolator(new DecelerateInterpolator());
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mMeasuredListener != null) {
                    mMeasuredListener.getMeasuredHeight(getMeasuredHeight());
                }
                if (Build.VERSION.SDK_INT < 16) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }


    /**
     * 伸缩状态
     */
    public boolean isExpand() {
        return isExpand;
    }

    private boolean isShrinking = false;
    private boolean isExpanding = false;

    public void shrink() {
        if (!isExpand || isShrinking || isExpanding) {
            return;
        }
        //Log.e("ZHZ","shrink");
        if (title != null){
            title.setVisibility(VISIBLE);
        }
        mShrinkAnim.setRawHeight(getMeasuredHeight());
        startAnimation(mShrinkAnim);
        colorAnimShrink.start();
    }

    public void expand(int height) {
        if (isExpand || getMeasuredHeight() == height || isExpanding || isShrinking) {
            return;
        }
        //isExpand = true;
        if (title != null){
            title.setVisibility(GONE);
        }

        //Log.e("ZHZ","expand");
        mExpandAnim.setRawHeight(height);
        startAnimation(mExpandAnim);
        colorAnimExpand.start();
    }

    /**
     * 界面返回至正常高度
     */
    private class ExpandAnim extends Animation {

        private int rawHeight;

        public void setRawHeight(int rawHeight) {
            this.rawHeight = rawHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int height = (int) (rawHeight * interpolatedTime);
            if (height > shrinkedHeight) {
                getLayoutParams().height = (int) (rawHeight * interpolatedTime);
            }
            requestLayout();
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }


    /**
     * 缩动画
     */
    private class ShrinkAnim extends Animation {

        private int rawHeight;

        public void setRawHeight(int rawHeight) {
            this.rawHeight = rawHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int height = rawHeight - (int) (rawHeight * interpolatedTime);
            if (height > shrinkedHeight) {
                getLayoutParams().height = rawHeight - (int) (rawHeight * interpolatedTime);
            }
            requestLayout(); // invalidate
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    private Animation.AnimationListener mShrinkAnimListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            isShrinking = true;
            if (mScrollView != null) {
                //mScrollView.setIntercept(true);
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            isExpand = false;
            isShrinking = false;
            if (mScrollView != null) {
                //mScrollView.setIntercept(false);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    private Animation.AnimationListener mExpandAnimListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            isExpanding = true;
            if (mScrollView != null) {
                mScrollView.setIntercept(true);
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            isExpand = true;
            isExpanding = false;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
}
