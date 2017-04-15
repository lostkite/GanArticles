package com.cambrian.android.ganarticles.views;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.cambrian.android.ganarticles.R;

/**
 * 用装饰者模式创建的 adapter, 添加了显示 foot view 的功能
 * Created by S.J.Xiong on 2017/3/9.
 */

public class ArticleListAdapterWrapper extends RecyclerView.Adapter {
    // 加载是显示的 item
    private static final int FOOT_ITEM = 0x05;

    private ArticleListAdapter mAdapter;

    public ArticleListAdapterWrapper(ArticleListAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mAdapter.getItemCount()) {
            return FOOT_ITEM;
        } else {
            return mAdapter.getItemViewType(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FOOT_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.foot_view, parent, false);
            return new FootViewHolder(view);
        } else {
            return mAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FootViewHolder) {
            ((FootViewHolder) holder).bindArticle();
        } else {
            mAdapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount() + 1;
    }

    private class FootViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar mProgressBar;
        FootViewHolder(View itemView) {
            super(itemView);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.recycler_progress_bar);
        }

        void bindArticle() {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }
}
