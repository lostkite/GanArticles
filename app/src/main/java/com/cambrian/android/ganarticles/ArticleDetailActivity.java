package com.cambrian.android.ganarticles;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.cambrian.android.ganarticles.enties.Article;
import com.cambrian.android.ganarticles.fragments.SingleWebViewFragment;

public class ArticleDetailActivity extends AppCompatActivity {
    private static final String ARTICLE = "article";
    public static Intent newIntent(Context packageContext, Article article) {
        Intent intent = new Intent(packageContext, ArticleDetailActivity.class);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment webViewFragment = fm.findFragmentById(R.id.single_fragment_container);
        if (webViewFragment == null) {
            webViewFragment = SingleWebViewFragment.getInstance(null);
        }
        fm.beginTransaction()
                .add(R.id.single_fragment_container, webViewFragment)
                .commit();
    }
}
