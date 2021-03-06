package com.guan.o2o.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.guan.o2o.R;
import com.guan.o2o.activity.ServiceNoteActivity;
import com.guan.o2o.adapter.PollPagerAdapter;
import com.guan.o2o.common.Constant;
import com.guan.o2o.model.WashOrder;
//import com.guan.o2o.utils.FuncUtil;
import com.guan.o2o.utils.FuncUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 主页Fragment
 *
 * @author Guan
 * @file com.guan.o2o.fragment
 * @date 2015/9/29
 * @Version 1.0
 */
public class HomeFragment extends FrameFragment {

    @InjectView(R.id.tv_city)
    TextView tvCity;
    @InjectView(R.id.iv_below)
    ImageView ivBelow;
    @InjectView(R.id.viewpager)
    public
    ViewPager viewpager;
    @InjectView(R.id.iv_a_wash)
    ImageView ivAWash;
    @InjectView(R.id.iv_bag_wash)
    ImageView ivBagWash;
    @InjectView(R.id.iv_home_ariticles)
    ImageView ivHomeAriticles;
    @InjectView(R.id.iv_other_wash)
    ImageView ivOtherWash;
    @InjectView(R.id.iv_service_note)
    ImageView ivServiceNote;
    @InjectView(R.id.llyt_dots)
    LinearLayout llytDots;

    private int mNum;
    private int[] imageUrls;
    private int mCurrentItem;
    private TextView mTvNum;
    private WashOrder washOrder;
    private ArrayList<View> mListViews;
    private PopupWindow mPopupWindow;
    private ImageView[] mImageViews;
    private ImageHandler mImageHandler;
    public LocationClient mLocationClient;
    // 定时周期执行指定的任务
    private ScheduledExecutorService mScheduledExecutorService;

    private OnClickListener mCallback;

    // 存放fragment的Activtiy必须实现的接口
    public interface OnClickListener {
        public void onIntentSelected(int position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // 为保证Activity容器实现以回调的接口,如果没会抛出一个异常。
        try {
            mCallback = (OnClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    /**
     * Handler来处理ViewPager的轮播,实现定时更新
     */
    private class ImageHandler extends Handler {

        private WeakReference<HomeFragment> mWeakReference;

        // 使用弱引用避免Handler泄露,泛型参数可以是Activity/Fragment
        public ImageHandler(HomeFragment fragment) {
            mWeakReference = new WeakReference<HomeFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.MSG_UPDATE_IMAGE:
                    mWeakReference.get().viewpager.setCurrentItem(mCurrentItem);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 开始轮播图切换
     */
    @Override
    public void onStart() {
        super.onStart();
        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        mScheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 2, 3, TimeUnit.SECONDS);
    }

    /**
     * 实现父类方法
     *
     * @param inflater
     * @return
     */
    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View _view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.inject(this, _view);
        return _view;
    }

    /**
     * 初始化变量
     */
    public void initVariable() {
        imageUrls = new int[]{
                R.mipmap.ic_poll_a, R.mipmap.ic_poll_c,
                R.mipmap.ic_poll_b, R.mipmap.ic_poll_d};
        washOrder = null;
        // 设定大大的值实现向左回播
        mCurrentItem = imageUrls.length * 1000;
        mImageHandler = new ImageHandler(HomeFragment.this);
        // 地图
        mLocationClient = new LocationClient(getActivity());
        // 设置监听
        mLocationClient.registerLocationListener(new MyLocationListener());
        // 初始化位置
        mLocationClient.setLocOption(FuncUtil.initLocation());
        /*
         * 初始化ViewPager
         */
        initViewPager();
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        LayoutInflater _inflater = LayoutInflater.from(getActivity());
        mListViews = new ArrayList<View>();
        mImageViews = new ImageView[imageUrls.length];

        for (int i = 0; i < imageUrls.length; i++) {
            // 图片
            View _view = (View) _inflater.inflate(R.layout.view_pager_null, null);
            _view.setBackgroundResource(imageUrls[i]);
            mListViews.add(_view);
            // 圆点
            mImageViews[i] = new ImageView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(16, 16);
            params.setMargins(7, 10, 7, 10);
            mImageViews[i].setLayoutParams(params);
            if (0 == i)
                mImageViews[i].setBackgroundResource(R.mipmap.ic_dot_c);
            else
                mImageViews[i].setBackgroundResource(R.mipmap.ic_dot);
            llytDots.addView(mImageViews[i]);
        }
    }

    /**
     * 绑定/设置数据操作
     */
    @Override
    public void bindData() {
        // 开始定位
        mLocationClient.start();
        // viewpager设置
        viewpager.setAdapter(new PollPagerAdapter(mListViews));
        viewpager.addOnPageChangeListener(new onPageChangeListener());
        viewpager.setCurrentItem(mCurrentItem);
    }

    /**
     * 轮询页面监听
     */
    private class onPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            // 设置当前位置，实现手动与自动轮播切换
            mCurrentItem = position;
            // 更新小圆点图标
            for (int i = 0; i < imageUrls.length; i++)
                if (position % imageUrls.length == i)
                    mImageViews[i].setBackgroundResource(R.mipmap.ic_dot_c);
                else
                    mImageViews[i].setBackgroundResource(R.mipmap.ic_dot);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int position) {

        }
    }

    /**
     * 执行轮播图切换任务
     */
    private class ScrollTask implements Runnable {

        @Override
        public void run() {
            synchronized (viewpager) {
                mCurrentItem++;
                // 通过handler切换图片
                mImageHandler.sendEmptyMessage(Constant.MSG_UPDATE_IMAGE);
            }
        }
    }

    /**
     * 监听实现
     */
    @OnClick({R.id.tv_city, R.id.iv_below, R.id.iv_a_wash, R.id.iv_bag_wash, R.id.iv_home_ariticles, R.id.iv_other_wash, R.id.iv_service_note})
    public void OnClick(View view) {

        switch (view.getId()) {
            case R.id.tv_city:
                break;

            case R.id.iv_below:
                break;

            case R.id.iv_a_wash:
                mCallback.onIntentSelected(Constant.CV_HOME_AWASH);
                break;

            case R.id.iv_bag_wash:
                // popwindow
                if (mPopupWindow != null && mPopupWindow.isShowing())
                    mPopupWindow.dismiss();
                else {
                    String textWash = getString(R.string.text_bag_wash);
                    showOrderWindowM(view, null, textWash, getString(R.string.price_abag));
                }
                break;

            case R.id.iv_home_ariticles:
                showTipsWindow(view,getString(R.string.pop_tip_title),getString(R.string.pop_tip_content2));
                break;

            case R.id.iv_other_wash:
                showTipsWindow(view,getString(R.string.pop_tip_title),getString(R.string.pop_tip_content2));
                break;

            case R.id.iv_service_note:
                openActivity(ServiceNoteActivity.class);
                break;

            default:
                break;
        }
    }

    /**
     * 位置监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation poiLocation) {
            tvCity.setText(poiLocation.getCity());
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    /**
     * 停止轮播图切换
     */
    @Override
    public void onStop() {
        super.onStop();
        mScheduledExecutorService.shutdown();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLocationClient.stop();
        ButterKnife.reset(this);
    }
}
