# RevealBanner
仿转转Banner  如有帮助，给个Star

![banner_g--.gif](https://upload-images.jianshu.io/upload_images/2119978-c95cfa61b70be78a.gif?imageMogr2/auto-orient/strip)

# 示例代码

- 在 app的build.gradle 里面引用

```
implementation 'com.tokiii:reveal-banner:1.0.1'
```

- 布局文件

```
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="240dp">

    <com.wikikii.bannerlib.banner.view.BannerBgContainer
        android:id="@+id/banner_bg_container"
        android:layout_width="match_parent"
        android:layout_height="240dp" />


    <com.wikikii.bannerlib.banner.LoopLayout
        android:id="@+id/loop_layout"
        android:layout_width="match_parent"
        android:layout_height="168dp"
        android:layout_gravity="bottom" />
</FrameLayout>
```

- 代码设置属性

```
        // 设置轮播图属性
        loopLayout.setLoop_ms(3000);//轮播的速度(毫秒)
        loopLayout.setLoop_duration(400);//滑动的速率(毫秒)
        loopLayout.setScaleAnimation(true);// 设置是否需要动画
        loopLayout.setLoop_style(LoopStyle.Empty);//轮播的样式-默认empty
        loopLayout.setIndicatorLocation(IndicatorLocation.Center);//指示器位置-中Center
        loopLayout.initializeData(this);
        // 设置轮播图属性end
        
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
        loopLayout.setOnBannerItemClickListener(this);
        if (bannerInfos.size() == 0) {
            return;
        }
        loopLayout.setLoopData(bannerInfos);// 设置轮播数据
        bannerBgContainer.setBannerBackBg(this, bgList);// 背景容器设置轮播图片
        loopLayout.setBannerBgContainer(bannerBgContainer);// 联动
        loopLayout.startLoop();// 开始循环
```

## License
```
 Copyright 2019, tokiii

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
