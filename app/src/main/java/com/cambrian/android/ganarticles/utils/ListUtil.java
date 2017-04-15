package com.cambrian.android.ganarticles.utils;

import com.cambrian.android.ganarticles.enties.Article;
import com.cambrian.android.ganarticles.enties.ComparetorArticle;

import java.util.Collections;
import java.util.List;

/**
 * Created by S.J.Xiong.
 */

public class ListUtil {
    public static void sortList(List<Article> articles) {
        Collections.sort(articles, new ComparetorArticle());
    }

    public static boolean isListEmpty(List list) {
        return list == null || list.isEmpty();
    }
}
