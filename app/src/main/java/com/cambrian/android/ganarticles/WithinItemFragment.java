package com.cambrian.android.ganarticles;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.cambrian.android.ganarticles.enties.Article;

import java.util.List;

/**
 * Created by S.J.Xiong.
 */

public abstract class WithinItemFragment extends AppFragment {
    @Override
    public void onStart() {
        super.onStart();
        CustomTabsClient.connectAndInitialize(getActivity(), "com.android.chrome");
    }

    protected void showDetail(Article article) {
        String packageName = "com.android.chrome";
        Intent browserIntent = new Intent();
        browserIntent.setPackage(packageName);
        List<ResolveInfo> activitiesList = getActivity().getPackageManager()
                .queryIntentActivities(browserIntent, -1);
        if (activitiesList.size() > 0) {
            openCustomTabs(article.getUrl());
        } else {
            Log.i("WithinItemFragment", "chrome application not exist");
            mListener.onShowArticleDetail(article);
        }
    }

    private void openCustomTabs(String url) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        PendingIntent pi = PendingIntent.getActivity(getActivity(), 0, shareIntent, 0);

        CustomTabsIntent intent = new CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                .setCloseButtonIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_arrow_back))
                .addMenuItem(getString(R.string.action_share_zh), pi)
                .build();
        intent.launchUrl(getActivity(), Uri.parse(url));
    }
}
