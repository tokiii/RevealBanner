package com.wikikii.bannerlib.banner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wikikii.bannerlib.R;
import com.wikikii.bannerlib.banner.bean.BannerInfo;
import com.wikikii.bannerlib.banner.listener.OnBannerItemClickListener;
import com.wikikii.bannerlib.banner.listener.OnLoadImageViewListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * LoopAdapterWrapper
 */
public class LoopAdapterWrapper extends PagerAdapter {
    private final Context context;
    private final ArrayList<BannerInfo> bannerInfos;//banner data
    private final OnBannerItemClickListener onBannerItemClickListener;
    private final OnLoadImageViewListener onLoadImageViewListener;

    private Map<Integer, View> pageMap = new HashMap<>();     //records all the pages in the ViewPager
    private boolean isAnimation;

    public void setAnimation(boolean animation) {
        isAnimation = animation;
    }

    public LoopAdapterWrapper(Context context, ArrayList<BannerInfo> bannerInfos, OnBannerItemClickListener onBannerItemClickListener, OnLoadImageViewListener onLoadImageViewListener) {
        this.context = context;
        this.bannerInfos = bannerInfos;
        this.onBannerItemClickListener = onBannerItemClickListener;
        this.onLoadImageViewListener = onLoadImageViewListener;
    }


    @Override
    public int getCount() {
        return Short.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        pageMap.remove(position);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final int index = position % bannerInfos.size();
        final BannerInfo bannerInfo = bannerInfos.get(index);
        View child = null;
        if (onLoadImageViewListener != null) {
            child = onLoadImageViewListener.createImageView(context, isAnimation);
            ImageView imageView = child.findViewById(R.id.iv_loop_banner);
            onLoadImageViewListener.onLoadImageView(imageView, bannerInfo.data);
            container.addView(child);
            container.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onBannerItemClickListener != null)
                        onBannerItemClickListener.onBannerClick(index, bannerInfos);

                }
            });
        } else {
            throw new NullPointerException("LoopViewPagerLayout onLoadImageViewListener is not initialize,Be sure to initialize the onLoadImageView");
        }
        pageMap.put(position, child);
        return child;
    }


    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        pageMap.put(position, (View) object);
    }

    //获去当前VIew的方法

    public View getPrimaryItem(int position) {
        return pageMap.get(position);
    }

}