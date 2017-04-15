package com.cambrian.android.ganarticles.enties;

import java.util.Comparator;

/**
 * Created on 2017/2/22.
 */

public class ComparetorArticle implements Comparator<Article> {

    /**
     * 根据{@link TypeEnum}顺序排序
     * @param article {@link Article}
     * @param otherArticle {@link Article}
     * @return -1, 0, 1
     */
    @Override
    public int compare(Article article, Article otherArticle) {
        return article.getType().compareTo(otherArticle.getType());
    }
}
