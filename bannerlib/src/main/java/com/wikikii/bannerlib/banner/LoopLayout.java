package com.wikikii.bannerlib.banner;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wikikii.bannerlib.R;
import com.wikikii.bannerlib.banner.bean.BannerInfo;
import com.wikikii.bannerlib.banner.listener.OnBannerItemClickListener;
import com.wikikii.bannerlib.banner.listener.OnLoadImageViewListener;
import com.wikikii.bannerlib.banner.util.L;
import com.wikikii.bannerlib.banner.util.Tools;
import com.wikikii.bannerlib.banner.view.BannerBgContainer;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * 滚动视图布局 内嵌ViewPager
 */
public class LoopLayout extends RelativeLayout {

    private ViewPager loopViewPager;
    private LinearLayout indicatorLayout;
    private LinearLayout animIndicatorLayout;
    private OnBannerItemClickListener onBannerItemClickListener = null;
    private OnLoadImageViewListener onLoadImageViewListener = null;
    private LoopAdapterWrapper loopAdapterWrapper;
    private int totalDistance;//Little red dot all the distance to move
    private int size = Tools.dip2px(getContext(), 8);//The size of the set point;
    private ArrayList<BannerInfo> bannerInfos;//banner data
    private ImageView animIndicator;//Little red dot on the move
    private ImageView[] indicators;//Initializes the white dots
    @DrawableRes
    private int normalBackground = R.drawable.indicator_normal_background;
    @DrawableRes
    private int selectedBackground = R.drawable.indicator_selected_background;
    private static final int MESSAGE_LOOP = 5;
    private int loop_ms = 4000;//loop speed(ms)
    private int loop_style = -1; //loop style(enum values[-1:empty,1:depth 2:zoom])
    private IndicatorLocation indicatorLocation = IndicatorLocation.Center; //Indicator Location(enum values[1:left,0:depth 2:right])
    private int loop_duration = 2000;//loop rate(ms)
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == MESSAGE_LOOP) {
                if (loopViewPager.getCurrentItem() < Short.MAX_VALUE - 1) {
                    loopViewPager.setCurrentItem(loopViewPager.getCurrentItem() + 1, true);
                    sendEmptyMessageDelayed(MESSAGE_LOOP, getLoop_ms());
                }
            }
        }
    };


    int mViewPagerIndex;

    boolean isScaleAnimation;

    public void setScaleAnimation(boolean scaleAnimation) {
        isScaleAnimation = scaleAnimation;
    }

    public LoopLayout(Context context) {
        super(context);
    }

    public LoopLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoopLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * onBannerItemClickListener
     *
     * @param onBannerItemClickListener onBannerItemClickListener
     */
    public void setOnBannerItemClickListener(OnBannerItemClickListener onBannerItemClickListener) {
        this.onBannerItemClickListener = onBannerItemClickListener;
    }

    /**
     * OnLoadImageViewListener
     *
     * @param onLoadImageViewListener onLoadImageViewListener
     */
    public void setOnLoadImageViewListener(OnLoadImageViewListener onLoadImageViewListener) {
        this.onLoadImageViewListener = onLoadImageViewListener;
    }

    private void initializeView() {
        float density = getResources().getDisplayMetrics().density;

        loopViewPager = new ViewPager(getContext());
        loopViewPager.setId(R.id.loop_viewpager);
        LayoutParams loop_params = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addView(loopViewPager, loop_params);

        // FrameLayout
        FrameLayout indicatorFrameLayout = new FrameLayout(getContext());
        LayoutParams f_params = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ((int) (20 * density)));
        f_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        f_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        switch (indicatorLocation) {
            case Left:
                f_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                break;
            case Right:
                f_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                break;
            default:
                break;
        }

        f_params.setMargins(((int) (10 * density)), 0, ((int) (10 * density)), 0);
        addView(indicatorFrameLayout, f_params);

        // 指标的布局
        indicatorLayout = new LinearLayout(getContext());
        FrameLayout.LayoutParams ind_params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
        indicatorLayout.setGravity(Gravity.CENTER);
        indicatorLayout.setOrientation(LinearLayout.HORIZONTAL);
        indicatorFrameLayout.addView(indicatorLayout, ind_params);

        // 动画指标布局
        animIndicatorLayout = new LinearLayout(getContext());
        FrameLayout.LayoutParams ind_params2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        animIndicatorLayout.setGravity(Gravity.CENTER | Gravity.START);
        animIndicatorLayout.setOrientation(LinearLayout.HORIZONTAL);
        indicatorFrameLayout.addView(animIndicatorLayout, ind_params2);
    }

    /**
     * 确保初始化数据
     *
     * @param context context
     */
    public void initializeData(Context context) {
        initializeView();

        L.e("LoopViewPager ---> initializeData");
        if (loop_duration > loop_ms) // 防止花屏
            loop_duration = loop_ms;

        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            LoopScroller mScroller;
            if (isScaleAnimation) {
                mScroller = new LoopScroller(context, new LinearInterpolator());

            } else {
                mScroller = new LoopScroller(context);
            }

            mScroller.setmDuration(loop_duration);
            mField.set(loopViewPager, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }

        loopViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        stopLoop(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        startLoop(true);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    /**
     * initialize the Data
     *
     * @param bannerInfos BannerInfo
     */
    public void setLoopData(ArrayList<BannerInfo> bannerInfos) {
        L.e("LoopViewPager ---> setLoopData");
        if (bannerInfos != null && bannerInfos.size() > 0) {
            this.bannerInfos = bannerInfos;
        } else {
            return;
        }
        if (indicatorLayout.getChildCount() > 0) {
            indicatorLayout.removeAllViews();
            removeView(animIndicator);
        }
        InitIndicator();
        InitLittleRed();
        totalDistance = 2 * size * (indicators.length - 1);
        loopAdapterWrapper = new LoopAdapterWrapper(getContext(), bannerInfos, onBannerItemClickListener, onLoadImageViewListener);
        loopAdapterWrapper.setAnimation(isScaleAnimation);
        loopViewPager.setAdapter(loopAdapterWrapper);
        loopViewPager.addOnPageChangeListener(new ViewPageChangeListener());
        int index = Short.MAX_VALUE / 2 - (Short.MAX_VALUE / 2) % bannerInfos.size();
        loopViewPager.setCurrentItem(index);
    }

    private void InitIndicator() {
        indicatorLayout.removeAllViews();
        indicators = new ImageView[bannerInfos.size()];
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            if (i != indicators.length - 1) {
                params.setMargins(0, 0, size, 0);
            } else {
                params.setMargins(0, 0, 0, 0);
            }
            indicators[i].setLayoutParams(params);
            indicators[i].setImageResource(R.drawable.icon_banner_indicator1);//设置默认的背景颜色
            indicatorLayout.addView(indicators[i]);
        }

    }

    private void InitLittleRed() {
        animIndicatorLayout.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        animIndicator = new ImageView(getContext());
//        animIndicator.setGravity(Gravity.CENTER);
        animIndicator.setImageResource(R.drawable.icon_banner_indicator0);//设置选中的背景颜色
        animIndicatorLayout.addView(animIndicator, params);
    }

    public int getLoop_ms() {
        if (loop_ms < 1500)
            loop_ms = 1500;
        return loop_ms;
    }

    /**
     * loop speed
     *
     * @param loop_ms (ms)
     */
    public void setLoop_ms(int loop_ms) {
        this.loop_ms = loop_ms;
    }

    /**
     * loop rate
     *
     * @param loop_duration (ms)
     */
    public void setLoop_duration(int loop_duration) {
        this.loop_duration = loop_duration;
    }

    /**
     * loop style
     *
     * @param loop_style (enum values[-1:empty,1:depth 2:zoom])
     */
    public void setLoop_style(LoopStyle loop_style) {
        this.loop_style = loop_style.getValue();
    }

    /**
     * 指示器的位置
     *
     * @param indicatorLocation (enum values[1:left,0:depth,2:right])
     */
    public void setIndicatorLocation(IndicatorLocation indicatorLocation) {
        this.indicatorLocation = indicatorLocation;
    }

    /**
     * startLoop
     */
    public void startLoop() {
        handler.removeCallbacksAndMessages(MESSAGE_LOOP);
        handler.sendEmptyMessageDelayed(MESSAGE_LOOP, getLoop_ms());
        L.e("LoopViewPager ---> startLoop");
//        if (loopAdapterWrapper.getPrimaryItem(loopViewPager.getCurrentItem()) != null && isScaleAnimation) {
//            ImageView imageView = loopAdapterWrapper.getPrimaryItem(loopViewPager.getCurrentItem()).findViewById(R.id.iv_loop_banner);
//            if (imageView != null) {
//                narrowView(imageView, loop_ms - 200);
//            }
//        }

    }


    /**
     * startLoop
     */
    public void startLoop(boolean isFinger) {
        handler.removeCallbacksAndMessages(MESSAGE_LOOP);
        handler.sendEmptyMessageDelayed(MESSAGE_LOOP, getLoop_ms());
        L.e("LoopViewPager ---> startLoop");

    }

    /**
     * stopLoop
     * 一定要在onDestroy中防止内存泄漏。
     */
    public void stopLoop() {
        handler.removeMessages(MESSAGE_LOOP);
        L.e("LoopViewPager ---> stopLoop");
//        if (loopAdapterWrapper.getPrimaryItem(loopViewPager.getCurrentItem()) != null && isScaleAnimation) {
//            ImageView imageView = loopAdapterWrapper.getPrimaryItem(loopViewPager.getCurrentItem()).findViewById(R.id.iv_loop_banner);
//            if (imageView != null) {
//                enLargeView(imageView);
//            }
//        }
    }

    /**
     * stopLoop
     * 一定要在onDestroy中防止内存泄漏。
     */
    public void stopLoop(boolean isFinger) {
        handler.removeMessages(MESSAGE_LOOP);
        L.e("LoopViewPager ---> stopLoop");

    }

    /**
     * LoopViewPager
     *
     * @return ViewPager
     */
    public ViewPager getLoopViewPager() {
        return loopViewPager;
    }

    public int getNormalBackground() {
        return normalBackground;
    }

    public void setNormalBackground(@DrawableRes int normalBackground) {
        this.normalBackground = normalBackground;
    }

    public int getSelectedBackground() {
        return selectedBackground;
    }

    public void setSelectedBackground(@DrawableRes int selectedBackground) {
        this.selectedBackground = selectedBackground;
    }


    BannerBgContainer bannerBgContainer;

    public void setBannerBgContainer(BannerBgContainer bannerBgContainer) {
        this.bannerBgContainer = bannerBgContainer;
    }


    float reduceValue = 0.2f;
    float upValue = 2.5f;
    boolean isMovingDone = false;

    /**
     * OnPageChangeListener
     */
    private class ViewPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (loopAdapterWrapper.getCount() > 0) {
                float length = ((position % bannerInfos.size()) + positionOffset) / (bannerInfos.size() - 1);
                if (length >= 1) {
                } else {
                    float path = length * totalDistance;
                    animIndicator.setTranslationX(path);
                }
                if (isScaleAnimation) {
                    ImageView imageView;
                    if (positionOffset == 0f) {
                        isMovingDone = true;
                        if (loopAdapterWrapper.getPrimaryItem(loopViewPager.getCurrentItem()) != null) {
                            imageView = loopAdapterWrapper.getPrimaryItem(loopViewPager.getCurrentItem()).findViewById(R.id.iv_loop_banner);
                            if (imageView != null) {
                                bigIndex = loopViewPager.getCurrentItem();
                                enLargeView(imageView);
                            }
                        }
                    } else {
                        isMovingDone = false;
                        if (bigIndex != -1) {
                            if (loopAdapterWrapper.getPrimaryItem(bigIndex) != null) {
                                imageView = loopAdapterWrapper.getPrimaryItem(bigIndex).findViewById(R.id.iv_loop_banner);
                                if (!delayAnimationSet.isRunning()) {
                                    delayAnimationSet.cancel();
                                    imageView.clearAnimation();
                                }
                                narrowView(imageView);
                            }

                        }
                    }
                }
            }

            if (bannerBgContainer == null) {
                return;
            }
            if (mViewPagerIndex == position) {
                if (bannerBgContainer.getBannerBgViews().size() > position % bannerBgContainer.getBannerBgViews().size() + 1) {
                    bannerBgContainer.getBannerBgViews().get(position % bannerBgContainer.getBannerBgViews().size() + 1).bringToFront();
                    bannerBgContainer.getBannerBgViews().get(position % bannerBgContainer.getBannerBgViews().size() + 1)
                            .hideClipAnimation((positionOffset - reduceValue) * upValue > 1 ? 1 : (positionOffset - reduceValue) * upValue);
                } else if (bannerBgContainer.getBannerBgViews().size() == position % bannerBgContainer.getBannerBgViews().size() + 1) {
                    bannerBgContainer.getBannerBgViews().get(0).bringToFront();
                    bannerBgContainer.getBannerBgViews().get(0)
                            .hideClipAnimation((positionOffset - reduceValue) * upValue > 1 ? 1 : (positionOffset - reduceValue) * upValue);
                }
            } else {
                if (position / bannerBgContainer.getBannerBgViews().size() >= 0) {
                    bannerBgContainer.getBannerBgViews().get(position % bannerBgContainer.getBannerBgViews().size()).bringToFront();
                    bannerBgContainer.getBannerBgViews().get(position % bannerBgContainer.getBannerBgViews().size())
                            .showClipAnimation(0, bannerBgContainer.getHeight() / 2,
                                    (1 - (positionOffset + reduceValue)) * upValue > 1 ? 1 : (1 - (positionOffset + reduceValue)) * upValue);
                }
            }

        }

        @Override
        public void onPageSelected(int position) {
            int i = position % bannerInfos.size();
            if (i == 0) {
                animIndicator.setTranslationX(totalDistance * 0.0f);
            } else if (i == bannerInfos.size() - 1) {
                animIndicator.setTranslationX(totalDistance * 1.0f);
            }
            mViewPagerIndex = position - 1;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == 1) {
                if (loopViewPager != null && isMovingDone) {
                    mViewPagerIndex = loopViewPager.getCurrentItem();
                }
            }
        }
    }


    AnimatorSet animatorSetsuofang = new AnimatorSet();
    ObjectAnimator scaleX;
    ObjectAnimator scaleY;

    AnimatorSet animatorSmall = new AnimatorSet();
    ObjectAnimator smallScaleX;
    ObjectAnimator smallScaleY;


    AnimatorSet delayAnimationSet = new AnimatorSet();
    ObjectAnimator delayScaleX;
    ObjectAnimator delayScaleY;
    int bigIndex;

    float smallScaleValue = 1.0f;
    float bigScaleValue = 1.2f;

    /**
     * 放大View
     *
     * @param view
     */
    public void enLargeView(final View view) {
        if (view.getScaleX() == smallScaleValue) {
            scaleX = ObjectAnimator.ofFloat(view, "scaleX", smallScaleValue, bigScaleValue);
            scaleY = ObjectAnimator.ofFloat(view, "scaleY", smallScaleValue, bigScaleValue);
            animatorSetsuofang.setDuration(200);
            animatorSetsuofang.setInterpolator(linearInterpolator);
            animatorSetsuofang.play(scaleX).with(scaleY);//两个动画同时开始
            animatorSetsuofang.start();
            animatorSetsuofang.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.clearAnimation();
                    animatorSetsuofang.removeAllListeners();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    /**
     * 缩小View
     *
     * @param view
     */
    public void narrowView(final View view) {
        if (view != null && view.getScaleX() == bigScaleValue) {
            smallScaleX = ObjectAnimator.ofFloat(view, "scaleX", bigScaleValue, smallScaleValue);
            smallScaleY = ObjectAnimator.ofFloat(view, "scaleY", bigScaleValue, smallScaleValue);
            animatorSmall.setDuration(100);
            animatorSmall.setInterpolator(linearInterpolator);
            animatorSmall.play(smallScaleX).with(smallScaleY);//两个动画同时开始
            animatorSmall.start();
            bigIndex = -1;
            animatorSmall.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.clearAnimation();
                    animatorSmall.removeAllListeners();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }


    /**
     * 缩小View
     *
     * @param view
     */
    public void narrowView(final View view, long delayTime) {
        if (view != null) {
            delayScaleX = ObjectAnimator.ofFloat(view, "scaleX", bigScaleValue, smallScaleValue);
            delayScaleY = ObjectAnimator.ofFloat(view, "scaleY", bigScaleValue, smallScaleValue);
            delayAnimationSet.setDuration(200);
            delayAnimationSet.setInterpolator(linearInterpolator);
            delayAnimationSet.play(delayScaleX).with(delayScaleY);//两个动画同时开始
            delayAnimationSet.setStartDelay(delayTime);
            delayAnimationSet.start();
            delayAnimationSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.clearAnimation();
                    delayAnimationSet.removeAllListeners();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    view.clearAnimation();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }


    LinearInterpolator linearInterpolator = new LinearInterpolator();

}
