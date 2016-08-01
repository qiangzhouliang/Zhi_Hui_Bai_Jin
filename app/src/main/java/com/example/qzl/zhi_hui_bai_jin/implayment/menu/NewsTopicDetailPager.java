package com.example.qzl.zhi_hui_bai_jin.implayment.menu;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.qzl.zhi_hui_bai_jin.base.BaseMenuDetailPager;

/**
 * 菜单详情页-专题
 * Created by Qzl on 2016-08-01.
 */
public class NewsTopicDetailPager extends BaseMenuDetailPager{
    public NewsTopicDetailPager(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {
        TextView view = new TextView(mActivity);
        view.setText("菜单详情页-专题");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);
        return view;
    }
}
