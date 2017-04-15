package com.cambrian.android.ganarticles.fragments;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.cambrian.android.ganarticles.R;
import com.cambrian.android.ganarticles.db.DBManager;
import com.cambrian.android.ganarticles.enties.Article;
import com.cambrian.android.ganarticles.enties.ObjectWrapperForBinder;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleWebViewFragment extends Fragment {
    private static final String ARTICLE_WRAPPER = "article_wrapper";
    private static final String APP_CACHE_DIR_NAME = "/webCache";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    webViewGoBack();
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    };

    private void webViewGoBack() {
        mWebView.goBack();
    }

    private Article mArticle;
    private WebView mWebView;
    private SwipeRefreshLayout mRefreshLayout;

    public static SingleWebViewFragment getInstance(Article article) {
        SingleWebViewFragment fragment = new SingleWebViewFragment();
        // 传递 Article, 存储
        Bundle args = new Bundle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // 利用 Binder 类传值仅支持 api 18 以上
            ObjectWrapperForBinder articleWrapper = new ObjectWrapperForBinder(article);
            args.putBinder(ARTICLE_WRAPPER, articleWrapper);
        } else {
            ArrayList<Article> arrayList = new ArrayList<>();
            arrayList.add(article);
            // 这里没有指定泛型，所以可以存储 List<Article> 的类型
            ArrayList listArrayList = new ArrayList();
            listArrayList.add(arrayList);
            args.putParcelableArrayList(ARTICLE_WRAPPER, listArrayList);
        }
        fragment.setArguments(args);

        return fragment;
    }


    public SingleWebViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                ObjectWrapperForBinder articleWrapper =
                        ((ObjectWrapperForBinder) getArguments()
                                .getBinder(ARTICLE_WRAPPER));
                mArticle = (Article) articleWrapper.getData();
            } else {
                ArrayList list = getArguments().getParcelableArrayList(ARTICLE_WRAPPER);
                List<Article> articleWrapper = (List<Article>) list.get(0);
                mArticle = articleWrapper.get(0);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_single_page, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.web_view_fragment_toolbar);
        toolbar.setTitle(mArticle.getDesc());

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        // 显示返回按钮
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.web_view_swipe_refresh_layout);

        mWebView = (WebView) view.findViewById(R.id.web_view);
        initWebView();

        // 初次加载 webView
        initSwipeRefreshLayout();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_for_webview, menu);
        MenuItem starItem = menu.findItem(R.id.action_star);
        CheckBox starCheckBox = (CheckBox) MenuItemCompat.getActionView(starItem);
        // 设置背景
        starCheckBox.setButtonDrawable(R.drawable.heart_checkbox);
        starCheckBox.setChecked(mArticle.isStarred());
        // 监听 check box 状态
        starCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mArticle.setStarred(true);
                    DBManager.getInstance().star(getActivity(), mArticle);
                } else {
                    DBManager.getInstance().unStar(getActivity(), mArticle);
                    mArticle.setStarred(false);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_open_browser:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mArticle.getUrl()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    getActivity().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    intent.setPackage(null);
                    getActivity().startActivity(intent);
                }
                return true;
            case R.id.action_star:
                return true;

            case R.id.action_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, mArticle.getUrl());
                getActivity().startActivity(Intent.createChooser(shareIntent, "分享给: "));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initSwipeRefreshLayout() {
        mRefreshLayout.setColorSchemeResources(
                R.color.grass_light,
                R.color.aqua_light,
                R.color.grapefruit_light,
                R.color.lavender_light
        );
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.loadUrl(mArticle.getUrl());
            }
        });
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                mWebView.loadUrl(mArticle.getUrl());
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // 支持放大缩小
        webSettings.setSupportZoom(true);
        // 出现放大缩小的按钮
        webSettings.setBuiltInZoomControls(true);
        // 设置web view推荐使用的窗口
        webSettings.setUseWideViewPort(true);
        // 设置web view加载的页面的模式
        webSettings.setLoadWithOverviewMode(true);

        // 开启并配置缓存
        String cacheDirPath = getActivity().getFilesDir().getAbsolutePath() + APP_CACHE_DIR_NAME;
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setAppCachePath(cacheDirPath);
        webSettings.setAppCacheEnabled(true);
        // 设置 web view 的滑动 style
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setWebViewClient(new WebViewClient() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            // 在加载结束时停止转动 refresh circle
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                    mHandler.sendEmptyMessage(1);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mWebView.stopLoading();
    }
}
