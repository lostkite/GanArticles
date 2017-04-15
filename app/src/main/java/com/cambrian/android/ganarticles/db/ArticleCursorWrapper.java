package com.cambrian.android.ganarticles.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.cambrian.android.ganarticles.db.ArticleDbSchema.ArticleTable;
import com.cambrian.android.ganarticles.enties.Article;
import com.cambrian.android.ganarticles.enties.TypeEnum;
import com.cambrian.android.ganarticles.utils.StringUtil;

import java.util.List;

/**
 * wrapper, 数据库辅助类
 *
 * Created by S.J.Xiong on 2017/3/9.
 */

public class ArticleCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public ArticleCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Article getArticle() {
        String idString = getString(getColumnIndex(ArticleTable.Cols.ID));
        String descString = getString(getColumnIndex(ArticleTable.Cols.DESC));
        String imageUrlString = getString(getColumnIndex(ArticleTable.Cols.IMAGE_URL));
        List<String> imageUrls = StringUtil.changeStringToList(imageUrlString);
        String publishedTime = getString(getColumnIndex(ArticleTable.Cols.PUBLISHED_TIME));
        String typeString = getString(getColumnIndex(ArticleTable.Cols.TYPE));
        TypeEnum type = TypeEnum.valueOf(typeString);
        String url = getString(getColumnIndex(ArticleTable.Cols.URL));
        boolean starred = getInt(getColumnIndex(ArticleTable.Cols.STARRED)) == 1;

        return new Article.Builder(idString, descString, type, url)
                .imageUrls(imageUrls)
                .publishedTime(publishedTime)
                .isStarred(starred)
                .build();
    }
}
