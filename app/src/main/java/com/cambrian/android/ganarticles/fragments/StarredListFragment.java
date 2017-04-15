package com.cambrian.android.ganarticles.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.cambrian.android.ganarticles.R;
import com.cambrian.android.ganarticles.WithinItemFragment;
import com.cambrian.android.ganarticles.db.DBManager;
import com.cambrian.android.ganarticles.enties.Article;
import com.cambrian.android.ganarticles.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * 用来显示已经收藏的文章
 */
public class StarredListFragment extends WithinItemFragment {
    private static final String ARTICLE_TYPE = "收藏";
    // 错误提示页面
    private TextView mErrorView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Article> mArticleList = new ArrayList<>();

    private boolean mLoading = false;

    public StarredListFragment() {
        // Required empty public constructor
    }

    public static StarredListFragment newInstance() {
        return new StarredListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.refresh_recycler_fragment, container, false);
        // Toolbar 的初始化
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(ARTICLE_TYPE);
        mListener.bindToolbar(toolbar);

        // SwipeRefreshLayout 和 ErrorView 的实例化
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.list_swipe_refresh_layout);
        initSwipeLayout();
        mErrorView = (TextView) view.findViewById(R.id.error_msg);
        // 加载 RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL));

        // 初次加载数据
        prepareData();
        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // 清空 menu 防止与 Activity 重影
        menu.clear();
        inflater.inflate(R.menu.menu_for_star_fragment, menu);
        // SearchView 的初始化
        MenuItem search = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        // 添加提交按钮
//        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * 当用户点击提交按钮或者按下键盘搜索键之后开始下载
             *
             * @param query 查询关键字
             * @return 事件响应
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 输入管理对象
                InputMethodManager imm =
                        (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    // 隐藏输入窗口
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                    searchView.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!mLoading) {
                    new DBConnectTask().execute(newText);
                    mLoading = true;
                }
                return true;
            }
        });
    }

    /**
     * 初次加载的数据
     */
    private void prepareData() {
        showMessage(StringUtil.Message.LOADING);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                new DBConnectTask().execute();
                mLoading = true;
            }
        });
    }

    /**
     * 初始化 swipeRefreshLayout
     */
    private void initSwipeLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.grass_light,
                R.color.aqua_light,
                R.color.grapefruit_light,
                R.color.lavender_light
        );
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!mLoading) {
                    startLoading();
                }
            }
        });
    }

    /**
     * 开启加载数据
     */
    private void startLoading() {
        if (!mLoading) {
            if (mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
            new DBConnectTask().execute();
            mLoading = true;
        }
    }


    /**
     * 加载数据结束时回调
     */
    private void finishLoading() {
        mLoading = false;
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (mArticleList.isEmpty()) {
            showMessage(StringUtil.Message.NULL_STARRED);
        } else {
            updateUI();
        }
    }

    private void showMessage(String msg) {
        mLoading = false;
        if (mRecyclerView != null
                && mRecyclerView.getVisibility() == View.VISIBLE) {
            mRecyclerView.setVisibility(View.GONE);
        }
        if (mErrorView != null) {
            mErrorView.setVisibility(View.VISIBLE);
            mErrorView.setText(msg);
        }
    }

    /**
     * articleList 显示
     */
    private void updateUI() {
        mLoading = false;
        if (mErrorView != null && mErrorView.getVisibility() == View.VISIBLE) {
            mErrorView.setVisibility(View.GONE);
        }
        if (mRecyclerView.getVisibility() == View.GONE) {
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        StarredListAdapter adapter = new StarredListAdapter();
        mRecyclerView.swapAdapter(adapter, false);
    }

    /**
     * 简化了 Item, 取消了图片显示
     */
    private class StarredItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Article mArticle;
        TextView descriptionTextView;
        CheckBox starCheckBox;
        StarredItemHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            descriptionTextView =
                    (TextView) itemView.findViewById(R.id.text_view_item_desc);
            starCheckBox =
                    (CheckBox) itemView.findViewById(R.id.checkbox_item_star);
        }

        void bindArticle(Article article) {
            mArticle = article;
            descriptionTextView.setText(article.getDesc());
            starCheckBox.setChecked(article.isStarred());
        }

        @Override
        public void onClick(View v) {
            showDetail(mArticle);
        }
    }

    private class StarredListAdapter
            extends RecyclerView.Adapter<StarredItemHolder> {

        @Override
        public StarredItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.item_normal, parent, false);
            return new StarredItemHolder(view);
        }

        @Override
        public void onBindViewHolder(final StarredItemHolder holder, int position) {
            final Article article = mArticleList.get(position);
            holder.bindArticle(article);
            holder.starCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        mArticleList.remove(article);
                        DBManager.getInstance().unStar(getContext(), article);
                        article.setStarred(false);
                        notifyItemRemoved(holder.getAdapterPosition());
                        // 修改recyclerView 的 item 数量
                        notifyItemRangeChanged(holder.getAdapterPosition(), getItemCount());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mArticleList.size();
        }
    }
    /**
     * 数据库连接线程
     */
    private class DBConnectTask extends AsyncTask<String, Void, List<Article>> {

        @Override
        protected List<Article> doInBackground(String... params) {
            List<Article> articleList;
            if (params.length > 0) {
                // String 比对
//                articleList = DBManager.getInstance().contains(getActivity(), params[0]);
                // 模糊查询
                articleList = DBManager.getInstance().query(getActivity(), params[0]);
            } else {
                articleList = DBManager.getInstance().listStar(getActivity());
            }
            return articleList;
        }

        @Override
        protected void onPostExecute(List<Article> articleList) {
            mArticleList = articleList;
            finishLoading();
        }
    }
}
