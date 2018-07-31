package com.wikikii.bannerlib.banner.listener;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

public interface OnLoadImageViewListener {
    /**
     * create image
     *
     * @param context context
     * @return image
     */
    View createImageView(Context context, boolean isScaleAnimation);

    /**
     * image load
     *
     * @param imageView ImageView
     * @param parameter String    可以为一个文件路径、uri或者url
     *                  Uri   uri类型
     *                  File  文件
     *                  Integer   资源Id,R.drawable.xxx或者R.mipmap.xxx
     *                  byte[]    类型
     *                  T 自定义类型
     */
    void onLoadImageView(ImageView imageView, Object parameter);
}
