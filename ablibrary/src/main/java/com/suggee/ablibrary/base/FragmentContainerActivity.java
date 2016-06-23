package com.suggee.ablibrary.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.github.pwittchen.reactivenetwork.library.ConnectivityStatus;

/**
 * Author:  wangchenghao
 * Email:   wangchenghao@howdo.cc | wangchenghao123@126.com
 * Date:    16/6/23
 * Description:
 */
public class FragmentContainerActivity extends BaseActivity {

    public static final String FRAGMENT_TAG = "FRAGMENT_CONTAINER";

    private static final String CLASS_NAME = "class_name";
    private static final String TYPE_FRAGMENT = "type_fragment";
    private static final String ARGS = "args";

    private Fragment mFragment = null;

    private android.support.v4.app.Fragment mV4Fragment = null;

    public static void launch(Context context, Class<?> clazz, Bundle args) {
        Intent intent = new Intent(context, FragmentContainerActivity.class);
        intent.putExtra(CLASS_NAME, clazz.getName());
        if (Fragment.class.isAssignableFrom(clazz)) {
            intent.putExtra(TYPE_FRAGMENT, "app");
        } else if (android.support.v4.app.Fragment.class.isAssignableFrom(clazz)) {
            intent.putExtra(TYPE_FRAGMENT, "v4");
        } else {
            throw new IllegalArgumentException("fragment must be instance of Fragment or android.support.v4.Fragment");
        }
        Bundle arg = new Bundle();
        if (args != null) {
            arg.putAll(args);
        }
        intent.putExtra(ARGS, arg);
        context.startActivity(intent);
    }

    public static void launchForResult(Activity from, Class<?> clazz, Bundle args, int requestCode) {
        Intent intent = new Intent(from, FragmentContainerActivity.class);
        intent.putExtra(CLASS_NAME, clazz.getName());
        if (Fragment.class.isAssignableFrom(clazz)) {
            intent.putExtra(TYPE_FRAGMENT, "app");
        } else if (android.support.v4.app.Fragment.class.isAssignableFrom(clazz)) {
            intent.putExtra(TYPE_FRAGMENT, "v4");
        } else {
            throw new IllegalArgumentException("fragment must be instance of Fragment or android.support.v4.Fragment");
        }
        Bundle arg = new Bundle();
        if (args != null) {
            arg.putAll(args);
        }
        intent.putExtra(ARGS, arg);
        from.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String className = getIntent().getStringExtra(CLASS_NAME);
        if (TextUtils.isEmpty(className)) {
            finish();
            return;
        }

        Bundle bundle = getIntent().getBundleExtra(ARGS);

        String type = getIntent().getStringExtra(TYPE_FRAGMENT);

        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            try {
                if ("app".equals(type)) {
                    mFragment = Fragment.instantiate(this, className);
                    if (bundle != null) {
                        mFragment.setArguments(bundle);
                    }
                } else if ("v4".equals(type)){
                    mV4Fragment = android.support.v4.app.Fragment.instantiate(this, className);
                    if (bundle != null) {
                        mV4Fragment.setArguments(bundle);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                finish();
                return;
            }

            if (mFragment != null) {
//                getFragmentManager().beginTransaction().add(R.id.common_fragment_container, mFragment, FRAGMENT_TAG).commit();
            }

            if (mV4Fragment != null) {
//                getSupportFragmentManager().beginTransaction().add(R.id.common_fragment_container, mV4Fragment, FRAGMENT_TAG).commit();
            }
        } else {
            if ("app".equals(type)) {
                mFragment = getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
            } else if ("v4".equals(type)) {
                mV4Fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
            }
        }
    }

    @Override
    protected int getContentViewLayoutID() {
        return 1;
//        return R.layout.fragment_common_container;
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {

    }

    @Override
    protected void onNetworkConnected(ConnectivityStatus status) {

    }

    @Override
    protected void onNetworkDisConnected() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String type = getIntent().getStringExtra(TYPE_FRAGMENT);
        if ("app".equals(type)) {
            if (mFragment != null) {
                mFragment.onActivityResult(requestCode, resultCode, data);
            }
        } else if ("v4".equals(type)) {
            if (mV4Fragment != null) {
                mV4Fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
