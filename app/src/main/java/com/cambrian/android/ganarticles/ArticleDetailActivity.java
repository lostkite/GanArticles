package com.cambrian.android.ganarticles;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.cambrian.android.ganarticles.enties.Article;
import com.cambrian.android.ganarticles.enties.ObjectWrapperForBinder;
import com.cambrian.android.ganarticles.fragments.SingleWebViewFragment;

import java.util.ArrayList;
import java.util.List;

public class ArticleDetailActivity extends AppCompatActivity {
    private static final String ARTICLE = "article";
    private static final String ARTICLE_WRAPPER = "article_wrapper";

    private Article mArticle;
    public static Intent newIntent(Context packageContext, Article article) {
        Intent intent = new Intent(packageContext, ArticleDetailActivity.class);
        // 传递 Article, 存储
        Bundle args = new Bundle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // 利用 Binder 类传值仅支持 api 18 以上
            ObjectWrapperForBinder articleWrapper = new ObjectWrapperForBinder(article);
            args.putBinder(ARTICLE_WRAPPER, articleWrapper);
            intent.putExtras(args);
        } else {
            ArrayList<Article> arrayList = new ArrayList<>();
            arrayList.add(article);
            // 这里没有指定泛型，所以可以存储 List<Article> 的类型
            ArrayList listArrayList = new ArrayList();
            listArrayList.add(arrayList);
            intent.putExtra(ARTICLE_WRAPPER, listArrayList);
        }
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        Intent intent = getIntent();
        if (intent != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                ObjectWrapperForBinder articleWrapper =
                        ((ObjectWrapperForBinder) intent.getExtras()
                                .getBinder(ARTICLE_WRAPPER));
                mArticle = (Article) articleWrapper.getData();
            } else {
                ArrayList list = intent.getParcelableExtra(ARTICLE_WRAPPER);
                List<Article> articleWrapper = (List<Article>) list.get(0);
                mArticle = articleWrapper.get(0);
            }
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment webViewFragment = fm.findFragmentById(R.id.single_fragment_container);
        if (webViewFragment == null) {
            webViewFragment = SingleWebViewFragment.getInstance(mArticle);
        }
        fm.beginTransaction()
                .add(R.id.single_fragment_container, webViewFragment)
                .commit();
    }
}
