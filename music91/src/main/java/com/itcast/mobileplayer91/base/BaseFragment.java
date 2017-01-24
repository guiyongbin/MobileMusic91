package com.itcast.mobileplayer91.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.itcast.mobileplayer91.R;
import com.itcast.mobileplayer91.utils.LogUtils;

/**
 * Created by Ding on 2016/10/10.
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener{

    private View root;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(getLayoutId(),null);
        initView();
        initListener();
        initData();

        registerCommonBtn();

        return root;
    }


    // 在多个界面都有的点击按钮，可以在这个方法注册监听
    private void registerCommonBtn() {
        View view = findViewById(R.id.back);
        if (view != null){
            view.setOnClickListener(this);
        }
    }

    /** 在当前Fragment使用的布局里查找控件*/
    protected View findViewById(int id) {
        return root.findViewById(id);
    }

    /** 返回当前 Activity 使用的布局id*/
    protected abstract int getLayoutId();

    /** 只允许执行 findViewById 操作*/
    protected abstract void initView() ;

    /** 执行注册监听、Adapter、广播，等解耦的操作*/
    protected abstract void initListener();

    /** 获取数据，初始化界面*/
    protected abstract void initData();

    /** 在 BaseActiviy 没有处理的点击事件，都在这个方法处理 */
    protected abstract void processClick(View v);

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                getFragmentManager().popBackStack();
                break;
            default:
                processClick(v);
                break;
        }
    }

    /** 显示一个内容为 msg 的吐司*/
    protected void toast(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    /** 打印一个等级为 error 的日志*/
    protected void logE(String msg) {
        LogUtils.e(getClass(), msg);
    }
}
