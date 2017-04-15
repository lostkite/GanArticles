package com.cambrian.android.ganarticles.fragments;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cambrian.android.ganarticles.BuildConfig;
import com.cambrian.android.ganarticles.R;
import com.cambrian.android.ganarticles.WithinItemFragment;
import com.cambrian.android.ganarticles.db.DBManager;
import com.cambrian.android.ganarticles.enties.Article;
import com.cambrian.android.ganarticles.enties.TypeEnum;
import com.cambrian.android.ganarticles.json.JsonParser;
import com.cambrian.android.ganarticles.thread.ArticleProvider;
import com.cambrian.android.ganarticles.thread.ArticleResult;
import com.cambrian.android.ganarticles.thread.DownloadCallback;
import com.cambrian.android.ganarticles.utils.DateUtil;
import com.cambrian.android.ganarticles.utils.ListUtil;
import com.cambrian.android.ganarticles.utils.StringUtil;
import com.cambrian.android.ganarticles.utils.UrlStringUtil;
import com.cambrian.android.ganarticles.views.ArticleListAdapter;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DailyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyFragment extends WithinItemFragment
        implements DownloadCallback<ArticleResult>, ArticleListAdapter.ArticlesBindCallback {
    private static final String TAG = "DailyFragment";

    // 页面
    private TextView mErrorView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Toolbar mToolbar;
    private Snackbar mSnackbar;
    // 数据
    private List<Article> mArticleList;
    private ArticleProvider mArticleProvider;
    private boolean mDownloading = false;
    private Date mDate;

    public DailyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DailyFragment.
     */
    public static DailyFragment newInstance() {
        return new DailyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mArticleProvider = new ArticleProvider(this);
        mDate = DateUtil.getAvailableDate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,
                              Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.refresh_recycler_fragment, container, false);
        // 初始化 Toolbar
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        updateToolbar();
        mListener.bindToolbar(mToolbar);

        // 初始化 SwipeRefreshLayout
        mSwipeRefreshLayout =
                (SwipeRefreshLayout) view.findViewById(R.id.list_swipe_refresh_layout);
        initSwipeLayout();
        mErrorView = (TextView) view.findViewById(R.id.error_msg);
        // 初始化 RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        prepareData();
        return view;
    }

    private void prepareData() {
        showMessage(StringUtil.Message.LOADING);
        String urlString = UrlStringUtil.getUrlString(mDate);
        startDownload(urlString);
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (hidden) {
            // 在fragment 隐藏时不显示 snackbar
            if (mSnackbar != null && mSnackbar.isShown()) {
                mSnackbar.dismiss();
            }
            if (mDownloading) {
                mArticleProvider.cancelDownload();
                mDownloading = false;
            }
        }
    }

    @Override
    public void updateFromDownload(ArticleResult result) {
        if (result == null) {
            showMessage("网络连接失败");
        } else if (result.getException() != null) {
            showMessage(result.getException().getMessage());
        } else if (result.getResultValue() != null) {
            if (mArticleList == null) {
                mArticleList = new ArrayList<>();
            } else {
                mArticleList.clear();
            }
            try {
                JsonParser.parseDailyItems(mArticleList, result.getResultValue());
                if (mArticleList == null || mArticleList.isEmpty()) {
                    showLastArticles("今天的干货可能未发布");
                } else {
                    showContent();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showMessage("解析数据出错");
            }
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
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

    private void startDownload(String urlString) {
        if (!mDownloading && mArticleProvider != null) {
            if (mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
            mDownloading = true;
            mArticleProvider.startDownload(urlString);
        }
    }

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

    private void showLastArticles(String msg) {
        if (mSnackbar == null) {
            mSnackbar = Snackbar.make(mSwipeRefreshLayout, msg, Snackbar.LENGTH_INDEFINITE)
                    .setAction(" 看看之前的干货吧", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDate = DateUtil.getLastDate(mDate);
                            startDownload(UrlStringUtil.getUrlString(mDate));
                        }
                    });
        }
        if (!isHidden()) {
            mSnackbar.show();
        }
    }

    private void showContent() {
        if (mErrorView != null) {
            mErrorView.setVisibility(View.GONE);
        }
        if (mRecyclerView.getVisibility() == View.GONE) {
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        updateToolbar();
        adapterRecyclerView();
    }

    private void updateToolbar() {
        mToolbar.setTitle(
                DateUtils.formatDateTime(getActivity(), mDate.getTime(),
                        DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE));
    }

    private void adapterRecyclerView() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "adapterRecyclerView attach " + isAdded());
        }
        DBManager.getInstance().markList(getActivity(), mArticleList);
        ListUtil.sortList(mArticleList);

        ArticleListAdapter adapter;
        if ((adapter = (ArticleListAdapter) mRecyclerView.getAdapter()) == null) {
            adapter = new ArticleListAdapter(mArticleList, this);
            adapter.setShowTitle(true);
            mRecyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

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
                if (mErrorView.getVisibility() == View.VISIBLE) {
                    mErrorView.setVisibility(View.GONE);
                }
                String urlString = UrlStringUtil.getUrlString(mDate);
                startDownload(urlString);
            }
        });

    }

    @Override
    public void showDetailArticle(Article article) {
        if (article.getType() == TypeEnum.福利) {
            ImageDialogFragment.newInstance(article.getUrl())
                    .show(getActivity().getSupportFragmentManager(), ImageDialogFragment.class.getSimpleName());
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
}
