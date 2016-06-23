package com.suggee.ablibrary.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.github.pwittchen.reactivenetwork.library.ConnectivityStatus;
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import com.suggee.ablibrary.R;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Author:  wangchenghao
 * Email:   wangchenghao@howdo.cc | wangchenghao123@126.com
 * Date:    16/6/22
 * Description:
 *              项目Activity基类，一切Activity都是继承自此
 */
public abstract class AppBaseActivity extends AppCompatActivity {

    /**
     * log tag
     */
    protected static String TAG;

    /**
     * 设置进入和转出动画模式(预设的几种模式)
     */
    public enum TransitionMode {
        LEFT, RIGHT, TOP, BOTTOM, SCALE, FADE
    }

    /**
     * 监听网络状态变化
     */
    private ReactiveNetwork mReactiveNetwork;

    private Subscription mNetworkConnectivitySubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        switch (getOverridePendingTransitionMode()) {
            case LEFT:
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                break;
            case RIGHT:
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                break;
            case TOP:
                overridePendingTransition(R.anim.top_in, R.anim.top_out);
                break;
            case BOTTOM:
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
                break;
            case SCALE:
                overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                break;
            case FADE:
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            default:
                break;
        }
        super.onCreate(savedInstanceState);

        TAG = this.getClass().getSimpleName();

        if (getContentViewLayoutID() != 0) {
            setContentView(getContentViewLayoutID());
        } else {
            throw new IllegalArgumentException("You must return a right contentView layout resource Id");
        }

        initViewsAndEvents(savedInstanceState);

        mReactiveNetwork = new ReactiveNetwork();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNetworkConnectivitySubscription != null && !mNetworkConnectivitySubscription.isUnsubscribed()) {
            mNetworkConnectivitySubscription.unsubscribe();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNetworkConnectivitySubscription = mReactiveNetwork.observeNetworkConnectivity(getApplicationContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ConnectivityStatus>() {
                    @Override
                    public void call(ConnectivityStatus status) {
                        if (status == ConnectivityStatus.UNKNOWN || status == ConnectivityStatus.OFFLINE) {
                            onNetworkDisConnected();
                        } else {
                            onNetworkConnected(status);
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        switch (getOverridePendingTransitionMode()) {
            case LEFT:
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                break;
            case RIGHT:
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                break;
            case TOP:
                overridePendingTransition(R.anim.top_in, R.anim.top_out);
                break;
            case BOTTOM:
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
                break;
            case SCALE:
                overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                break;
            case FADE:
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            default:
                break;
        }
    }

    /**
     * 设置进入和转出动画模式,默认是FADE
     *
     * @return
     */
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.FADE;
    }

    /**
     * 设置layoutID
     *
     * @return
     */
    protected abstract int getContentViewLayoutID();

    /**
     * 初始化view以及设置一些events
     *
     * @param savedInstanceState
     */
    protected abstract void initViewsAndEvents(Bundle savedInstanceState);

    /**
     * 设置loading、error、network等状态的targetView
     *
     * @return
     */
    protected abstract View getLoadingTargetView();

    /**
     * 网络连接上，有网状态
     *
     * @param status 网络类型 1.wifi 2.mobile
     */
    protected abstract void onNetworkConnected(ConnectivityStatus status);

    /**
     * 网络断线，无网状态
     */
    protected abstract void onNetworkDisConnected();

}
