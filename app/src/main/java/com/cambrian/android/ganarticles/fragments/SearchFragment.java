package com.cambrian.android.ganarticles.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.TextView;

import com.cambrian.android.ganarticles.R;
import com.cambrian.android.ganarticles.WithinItemFragment;
import com.cambrian.android.ganarticles.db.DBManager;
import com.cambrian.android.ganarticles.enties.Article;
import com.cambrian.android.ganarticles.json.JsonParser;
import com.cambrian.android.ganarticles.thread.ArticleProvider;
import com.cambrian.android.ganarticles.thread.ArticleResult;
import com.cambrian.android.ganarticles.thread.DownloadCallback;
import com.cambrian.android.ganarticles.utils.UrlStringUtil;
import com.cambrian.android.ganarticles.views.ArticleListAdapter;
import com.cambrian.android.ganarticles.views.ArticleListAdapterWrapper;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S.J.Xiong.
 */

public class SearchFragment extends WithinItemFragment
        implements DownloadCallback<ArticleResult>, ArticleListAdapter.ArticlesBindCallback {

    private TextView mMsgView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Article> mArticleList;
    private ArticleProvider mArticleProvider;
    private boolean mDownloading = false;
    private int mLoadTimes = 1;
    private boolean mLoading = false;

    public static SearchFragment getInstance() {
        return new SearchFragment();
    }

    public SearchFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // 初始化
        mArticleProvider = new ArticleProvider(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.refresh_recycler_fragment, container, false);
        // 初始化 Toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mMsgView = (TextView) view.findViewById(R.id.error_msg);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.list_swipe_refresh_layout);
        initSwipeLayout();
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
                        String urlString = UrlStringUtil.getSearchUrlString(mLoadTimes);
                        startDownload(urlString);
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_for_star_fragment, menu);
        // 实现搜索栏的功能
        MenuItem searchItem = menu.findItem(R.id.action_search);
        // 默认伸展搜索栏
        searchItem.expandActionView();
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mListener.onFragmentPop();
                return true;
            }
        });
        final SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView.setIconifiedByDefault(false);
//        // 添加提交按钮
//        searchView.setSubmitButtonEnabled(true);
        // 实现搜索功能
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
                // 开始下载
                String urlString = UrlStringUtil.getSearchUrlString(query, 1);
                startDownload(urlString);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 可以进行预加载
                return false;
            }
        });
//        // 关闭搜索栏时的处理
//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                mListener.onFragmentPop();
//                return true;
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mListener.onFragmentPop();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    /**
     * 开始下载任务并且将界面状态变为下载状态
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
    public void updateFromDownload(ArticleResult result) {
        if (result == null) {
            showMessage("网络连接失败");
        } else if (result.getException() != null) {
            showMessage(result.getException().getMessage());
        } else if (result.getResultValue() != null) {
            List<Article> articleList = new ArrayList<>();
            try {
                JsonParser.parseSearchItems(articleList, result.getResultValue());
                // 装配数据以待显示
                assemblyList(articleList);
            } catch (JSONException e) {
                e.printStackTrace();
                showMessage("解析数据时发生错误：\n" + e.getMessage());
            }
        }
    }

    /**
     * 将网络获取的数据放入 mArticles 之中
     *
     * @param articleList model list
     */
    private void assemblyList(List<Article> articleList) {
        if (mArticleList == null) {
            mArticleList = new ArrayList<>();
        }
        // 区分刷新操作和加载更多操作
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
     * 提示信息
     *
     * @param msg String
     */
    private void showMessage(String msg) {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (mRecyclerView != null) {
            mRecyclerView.setVisibility(View.GONE);
        }
        mMsgView.setVisibility(View.VISIBLE);
        mMsgView.setText(getString(R.string.data_unavailable_message, msg));
    }

    /**
     * 通过 RecyclerView 显示数据列表
     */
    private void showList() {
        if (mMsgView != null) {
            mMsgView.setVisibility(View.GONE);
        }
        if (mRecyclerView.getVisibility() == View.GONE) {
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        adapterRecyclerView();
    }

    /**
     * 给 RecyclerView 绑定 adapter
     */
    private void adapterRecyclerView() {
        DBManager.getInstance().markList(getActivity(), mArticleList);

        ArticleListAdapterWrapper adapterWrapper;
        if ((adapterWrapper = ((ArticleListAdapterWrapper) mRecyclerView.getAdapter())) == null) {
            ArticleListAdapter newAdapter = new ArticleListAdapter(mArticleList, this);
            newAdapter.setShowTitle(false);
            adapterWrapper = new ArticleListAdapterWrapper(newAdapter);
            mRecyclerView.swapAdapter(adapterWrapper, false);
        } else {
            adapterWrapper.notifyDataSetChanged();
        }
    }

    /**
     * 创建一个新的 Fragment 并添加进回退栈
     *
     * @param article article
     */
    @Override
    public void showDetailArticle(Article article) {
        showDetail(article);
    }

    /**
     * 收藏文章或者取消收藏
     *
     * @param starred star state
     * @param article article
     */
    @Override
    public void onStarStateChanged(boolean starred, Article article) {
        if (starred) {
            DBManager.getInstance().star(getActivity(), article);
        } else {
            DBManager.getInstance().unStar(getActivity(), article);
        }
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
                    startDownload(UrlStringUtil.getSearchUrlString(mLoadTimes));
                }
            }
        });
    }
}
