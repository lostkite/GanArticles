package com.cambrian.android.ganarticles.fragments;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cambrian.android.ganarticles.R;
import com.cambrian.android.ganarticles.WithinItemFragment;
import com.cambrian.android.ganarticles.db.DBManager;
import com.cambrian.android.ganarticles.enties.Article;
import com.cambrian.android.ganarticles.enties.TypeEnum;
import com.cambrian.android.ganarticles.json.JsonParser;
import com.cambrian.android.ganarticles.thread.ArticleProvider;
import com.cambrian.android.ganarticles.thread.ArticleResult;
import com.cambrian.android.ganarticles.thread.DownloadCallback;
import com.cambrian.android.ganarticles.utils.StringUtil;
import com.cambrian.android.ganarticles.utils.UrlStringUtil;
import com.cambrian.android.ganarticles.views.ArticleListAdapter;
import com.cambrian.android.ganarticles.views.ArticleListAdapterWrapper;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends WithinItemFragment
        implements DownloadCallback<ArticleResult>, ArticleListAdapter.ArticlesBindCallback {
    private static final String ARTICLE_TYPE = "type";

    private String mType;
    // 错误提示页面
    private TextView mErrorView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArticleProvider mArticleProvider;
    private boolean mDownloading = false;

    private int mLoadTimes = 1;
    private boolean mLoading = false;
    private List<Article> mArticleList;

    public CategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CategoryFragment.
     */
    public static CategoryFragment newInstance(String type) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARTICLE_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mType = getArguments().getString(ARTICLE_TYPE);
        }
        mArticleProvider = new ArticleProvider(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.refresh_recycler_fragment, container, false);
        // 初始化 Toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(mType);
        mListener.bindToolbar(toolbar);
        // 初始化 SwipeRefreshLayout ErrorView
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.list_swipe_refresh_layout);
        initSwipeLayout();
        mErrorView = (TextView) view.findViewById(R.id.error_msg);

        // 装载 RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        // 监听滑动事件实现上拉加载
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 滑动结束时才进行处理
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 当前显示的最后一个 item 的位置
                    int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager())
                            .findLastVisibleItemPosition();
                    // position 从0开始
                    if (lastVisibleItem + 1 == recyclerView.getAdapter().getItemCount()) {
                        mLoadTimes += 1;
                        mLoading = true;
                        String urlString = UrlStringUtil.getUrlString(mType, mLoadTimes);
                        startDownload(urlString);
                    }
                }
            }
        });

        // 显示初始数据
        prepareData();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_with_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                SearchFragment searchFragment = SearchFragment.getInstance();
                mListener.onFragmentChange("search", searchFragment);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void updateFromDownload(ArticleResult result) {
        if (result == null) {
            showMessage("网络连接失败");
        } else if (result.getException() != null) {
            showMessage(result.getException().getMessage());
        } else if (result.getResultValue() != null) {
            List<Article> moreArticles = new ArrayList<>();
            try {
                JsonParser.parseCategoryItems(moreArticles, result.getResultValue());
                // 装配数据
                assemblyList(moreArticles);
            } catch (JSONException e) {
                e.printStackTrace();
                showMessage("解析数据失败");
            }
        }
    }

    /**
     * 将网络获取的数据放入 mArticles
     *
     * @param articleList list
     */
    private void assemblyList(List<Article> articleList) {
        if (mArticleList == null) {
            mArticleList = new ArrayList<>();
        }
        if (mLoadTimes <= 1) {
            mArticleList.clear();
            mArticleList.addAll(articleList);
        } else {
            mArticleList.addAll(articleList);
            mLoading = false;
        }
        if (mArticleList.isEmpty()) {
            showMessage("获取数据失败");
        } else {
            showList();
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    /**
     * 加载数据的界面显示
     *
     * @param urlString url
     */
    private void startDownload(String urlString) {
        if (!mDownloading && mArticleProvider != null) {
            if (!mLoading && mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
            mDownloading = true;
            mArticleProvider.startDownload(urlString);
        }
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (mArticleProvider != null) {
            mArticleProvider.cancelDownload();
        }
    }

    /**
     * 界面提示信息
     *
     * @param msg 信息
     */
    private void showMessage(String msg) {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (mRecyclerView != null) {
            mRecyclerView.setVisibility(View.GONE);
        }
        mErrorView.setVisibility(View.VISIBLE);
        mErrorView.setText(getString(R.string.data_unavailable_message, msg));
    }

    private void showList() {
        if (mErrorView != null) {
            mErrorView.setVisibility(View.GONE);
        }
        if (mRecyclerView.getVisibility() == View.GONE) {
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        adapterRecyclerView();
    }

    /**
     * 绑定 adapter
     */
    private void adapterRecyclerView() {
        DBManager.getInstance().markList(getActivity(), mArticleList);

        ArticleListAdapterWrapper adapterWrapper;
        if ((adapterWrapper = (ArticleListAdapterWrapper) mRecyclerView.getAdapter()) == null) {
            ArticleListAdapter newAdapter = new ArticleListAdapter(mArticleList, this);
            newAdapter.setShowTitle(false);
            adapterWrapper = new ArticleListAdapterWrapper(newAdapter);
            mRecyclerView.setAdapter(adapterWrapper);
        } else {
            adapterWrapper.notifyDataSetChanged();
        }
    }

    @Override
    public void showDetailArticle(Article article) {
        if (article.getType() == TypeEnum.福利) {
            ImageDialogFragment fragment = ImageDialogFragment.newInstance(article.getUrl());
            fragment.show(getActivity().getSupportFragmentManager(), fragment.getClass().getSimpleName());
        } else {
            showDetail(article);
        }
    }

    @Override
    public void onStarStateChanged(boolean starred, Article article) {
        if (starred) {
            DBManager.getInstance().star(getActivity(), article);
        } else {
            DBManager.getInstance().unStar(getActivity(), article);
        }
    }

    /**
     * 显示初始化数据
     */
    private void prepareData() {
        showMessage(StringUtil.Message.LOADING);
        String urlString = UrlStringUtil.getUrlString(mType, 1);
        startDownload(urlString);
    }

    /**
     * 初始化 swipeRefreshLayout, 同时开启多线程获取数据
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
                if (!mDownloading) {
                    mLoadTimes = 1;
                    startDownload(UrlStringUtil.getUrlString(mType, 1));
                }
            }
        });
    }
}
