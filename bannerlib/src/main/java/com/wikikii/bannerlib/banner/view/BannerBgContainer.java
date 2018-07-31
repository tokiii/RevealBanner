package com.wikikii.bannerlib.banner.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.wikikii.bannerlib.banner.util.DensityUtil;
import com.wikikii.bannerlib.banner.view.BannerBgView;

import java.util.ArrayList;
import java.util.List;

/**
 * banner 背景容器
 */
public class BannerBgContainer extends RelativeLayout {

    private List<BannerBgView> bannerBgViews = new ArrayList<>();


    public BannerBgContainer(Context context) {
        super(context);
    }

    public BannerBgContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BannerBgContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public List<BannerBgView> getBannerBgViews() {
        return this.bannerBgViews;
    }

    /**
     * 设置
     *
     * @param context
     * @param bgUrlList
     */
    public void setBannerBackBg(Context context, List<Object> bgUrlList) {
        bannerBgViews.clear();
        this.removeAllViews();
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, DensityUtil.dp2px(240));
        layoutParams.leftMargin = -DensityUtil.dp2px(20);
        layoutParams.rightMargin = -DensityUtil.dp2px(20);
        for (Object urlImageView : bgUrlList) {
            BannerBgView bannerBgView = new BannerBgView(context);
            bannerBgView.setLayoutParams(layoutParams);
            Glide.with(context).load(urlImageView).into(bannerBgView.getImageView());
            bannerBgViews.add(bannerBgView);
            this.addView(bannerBgView);
        }
        bannerBgViews.get(0).bringToFront();
    }


}
