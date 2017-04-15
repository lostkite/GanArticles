package com.cambrian.android.ganarticles;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.cambrian.android.ganarticles.enties.Article;


/**
 * BaseFragment, 定义了一个与 Activity 交互的接口
 */

public abstract class AppFragment extends Fragment{

    protected OnFragmentChangeListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentChangeListener) {
            mListener = (OnFragmentChangeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implements OnFragmentChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * 实现Fragment与Activity的交互
     *
     * 由包含此Fragment或此Fragment的子类的Activity实现
     */
    public interface OnFragmentChangeListener {

        void onShowArticleDetail(Article article);

        /**
         * 切换fragment
         *
         * @param type type of the new fragment
         * @param newFragment 新的fragment
         */
        void onFragmentChange(String type, Fragment newFragment);

        /**
         * 从当前fragment退出
         */
        void onFragmentPop();

        /**
         * 绑定 toolbar
         * @param toolbar toolbar of fragment
         */
        void bindToolbar(Toolbar toolbar);
    }
}
