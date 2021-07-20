package com.wikikii.revealbanner;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wikikii.bannerlib.banner.IndicatorLocation;
import com.wikikii.bannerlib.banner.LoopLayout;
import com.wikikii.bannerlib.banner.LoopStyle;
import com.wikikii.bannerlib.banner.OnDefaultImageViewLoader;
import com.wikikii.bannerlib.banner.bean.BannerInfo;
import com.wikikii.bannerlib.banner.view.BannerBgContainer;
import com.wikikii.revealbanner.bean.BannerListBean;

import java.util.ArrayList;
import java.util.List;

public class BannerListAdapter extends BaseQuickAdapter<BannerListBean, BaseViewHolder> {


    public BannerListAdapter(int layoutResId, @Nullable List<BannerListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BannerListBean item) {
        LoopLayout loopLayout = helper.getView(R.id.loop_layout);
        BannerBgContainer bannerBgContainer = helper.getView(R.id.banner_bg_container);
        int currentPosition = getData().indexOf(item);// 获取当前的位置
        if (loopLayout.getTag(R.id.loop_layout) == null) {// 如果没设置过tag 再初始化banner
            loopLayout.setTag(R.id.loop_layout, currentPosition);
            setLoop(loopLayout, bannerBgContainer);
        }
    }


    private void setLoop(LoopLayout loopLayout, BannerBgContainer bannerBgContainer) {
        loopLayout.setLoop_ms(3000);//轮播的速度(毫秒)
        loopLayout.setLoop_duration(400);//滑动的速率(毫秒)
        loopLayout.setScaleAnimation(false);// 设置是否需要动画
        loopLayout.setLoop_style(LoopStyle.Empty);//轮播的样式-默认empty
        loopLayout.setIndicatorLocation(IndicatorLocation.Center);//指示器位置-中Center
        loopLayout.initializeData(mContext);
        // 准备数据
        ArrayList<BannerInfo> bannerInfos = new ArrayList<>();
        List<Object> bgList = new ArrayList<>();
        bannerInfos.add(new BannerInfo(R.mipmap.banner_1, "first"));
        bannerInfos.add(new BannerInfo(R.mipmap.banner_2, "second"));
        bgList.add(R.mipmap.banner_bg1);
        bgList.add(R.mipmap.banner_bg2);
        // 设置监听
        loopLayout.setOnLoadImageViewListener(new OnDefaultImageViewLoader() {
            @Override
            public void onLoadImageView(ImageView view, Object object) {
                Glide.with(view.getContext())
                        .load(object)
                        .into(view);
            }
        });
        loopLayout.setLoopData(bannerInfos);
        bannerBgContainer.setBannerBackBg(mContext, bgList);
        loopLayout.setBannerBgContainer(bannerBgContainer);
        loopLayout.startLoop();
    }
}
