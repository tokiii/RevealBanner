package com.wikikii.revealbanner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wikikii.revealbanner.bean.BannerListBean;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity {

    RecyclerView rvBannerList;
    SwipeRefreshLayout slBannerList;

    BannerListAdapter bannerListAdapter;

    private List<BannerListBean> allBeans = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        initView();
    }

    private void initView() {
        rvBannerList = findViewById(R.id.rv_banner_list);
        slBannerList = findViewById(R.id.sl_banner_list);
        bannerListAdapter = new BannerListAdapter(R.layout.item_banner, allBeans);
        rvBannerList.setLayoutManager(new LinearLayoutManager(this));
        rvBannerList.setAdapter(bannerListAdapter);

        for (int i = 0; i < 10; i++){
            BannerListBean bannerListBean = new BannerListBean();
            allBeans.add(bannerListBean);
        }
        bannerListAdapter.notifyDataSetChanged();

        // 刷新列表
        slBannerList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                slBannerList.setRefreshing(false);
                bannerListAdapter.notifyDataSetChanged();
            }
        });
    }
}
