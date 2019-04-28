package com.wikikii.bannerlib.banner;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Scroller
 * 更好的用户体验
 */
public class LoopScroller extends Scroller {
    private int mDuration = 1000;//速率必须小于延迟时间loop_ms

    public LoopScroller(Context context) {
        super(context);
    }

    public LoopScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public LoopScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }


    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    public void setmDuration(int time) {
        mDuration = time;
    }

    public int getmDuration() {
        return mDuration;
    }

}
