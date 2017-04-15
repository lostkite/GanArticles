package com.cambrian.android.ganarticles.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cambrian.android.ganarticles.db.ArticleDbSchema.ArticleTable;
import com.cambrian.android.ganarticles.enties.Article;
import com.cambrian.android.ganarticles.utils.StringUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * DBUtil 与数据库链接相关的类
 * <p>
 * Created by S.J.Xiong on 2017/3/7.
 */

public class DBManager {

    private volatile static DBManager instance;
    private WeakReference<List<Article>> mArticleList;
    private WeakReference<List<String>> mStarredId;

    public static DBManager getInstance() {
        if (instance == null) {
            synchronized (DBManager.class) {
                if (instance == null) {
                    instance = new DBManager();
                }
            }
        }

        return instance;
    }

    private DBManager() {
        mStarredId = new WeakReference<List<String>>(new ArrayList<String>());
        mArticleList = new WeakReference<List<Article>>(new ArrayList<Article>());
    }

    public void markList(Context context, List<Article> articles) {
        List<String> articleId = mStarredId.get();
        if (articleId == null || articleId.isEmpty()) {
            articleId = listStarredArticleId(context);
        }
        for (Article article : articles) {
            if (articleId.contains(article.getId())) {
                article.setStarred(true);
            }
        }
    }

    public List<Article> listStar(Context context) {
        List<Article> articles = mArticleList.get();
        if (articles == null || articles.isEmpty()) {
            articles = listStarredArticle(context);
        }
        return articles;
    }

    public List<Article> contains(Context context, String value) {
//        String keyword = StringUtil.bytesToHexString(value.getBytes());
        List<Article> newArticle = new ArrayList<>();
        List<Article> articles = mArticleList.get();
        if (articles == null || articles.isEmpty()) {
            articles = listStarredArticle(context);
        }

        for (Article article : articles) {
            if (article.getDesc().contains(value)) {
                newArticle.add(article);
            }
        }

        return newArticle;

    }

    public boolean star(Context context, Article article) {
        String id = article.getId();
        List<String> articleId = mStarredId.get();
        if (articleId == null || articleId.isEmpty()) {
            articleId = listStarredArticleId(context);
        }
        if (!articleId.contains(id)) {
            articleId.add(id);
            mStarredId = new WeakReference<>(articleId);
            return save(context, article);
        }
        return true;
    }

    public boolean unStar(Context context, Article article) {
        String id = article.getId();
        List<String> articleId = mStarredId.get();
        if (articleId == null || articleId.isEmpty()) {
            articleId = listStarredArticleId(context);
        }
        if (articleId.contains(id)) {
            articleId.remove(id);
            mStarredId = new WeakReference<>(articleId);
            return remove(context, article);
        }
        return false;
    }

    /**
     * 模糊查询匹配
     *
     * @param context 关键字
     * @param value
     * @return article list
     */
    public List<Article> query(Context context, String value) {
        List<Article> articleList = new ArrayList<>();
        if (value == null) {
            return articleList;
        } else if (value.length() == 0) {
            articleList = mArticleList.get();
            if (articleList == null || articleList.isEmpty()) {
                articleList = listStarredArticle(context);
            }
        } else {
            articleList = getMatchArticles(context, value);
        }

        return articleList;
    }

    private List<Article> listStarredArticle(Context context) {
        List<Article> articleList = new ArrayList<>();
        SQLiteDatabase db = new ArticleDatabaseHelper(context).getReadableDatabase();

        String[] projection = new String[]{
                ArticleTable.Cols.ID,
                ArticleTable.Cols.DESC,
                ArticleTable.Cols.IMAGE_URL,
                ArticleTable.Cols.TYPE,
                ArticleTable.Cols.URL,
                ArticleTable.Cols.PUBLISHED_TIME,
                ArticleTable.Cols.STARRED
        };

        Cursor cursor = db.query(ArticleTable.NAME, projection, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            ArticleCursorWrapper cursorWrapper = new ArticleCursorWrapper(cursor);
            while (cursor.moveToNext()) {
                Article article = cursorWrapper.getArticle();
                articleList.add(article);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        mArticleList = new WeakReference<>(articleList);
        Log.d("list articles ", "size is " + articleList.size());
        return articleList;
    }

    private List<String> listStarredArticleId(Context context) {
        List<String> articleIds = new ArrayList<>();
        SQLiteDatabase db = new ArticleDatabaseHelper(context).getReadableDatabase();

        String[] projection = new String[]{
                ArticleTable.Cols.ID
        };

        Cursor cursor = db.query(ArticleTable.NAME, projection, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ArticleTable.Cols.ID));
                articleIds.add(id);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        mStarredId = new WeakReference<>(articleIds);
        Log.d("mark articles ", "size is " + articleIds.size());

        return articleIds;
    }

    private boolean save(Context context, Article article) {
        SQLiteDatabase db = new ArticleDatabaseHelper(context).getReadableDatabase();

        long update = db.insert(ArticleTable.NAME, null, getContentValues(article));
        db.close();
        return update != -1;
    }

    private boolean remove(Context context, Article article) {
        SQLiteDatabase db = new ArticleDatabaseHelper(context).getReadableDatabase();

        int update = db.delete(ArticleTable.NAME,
                ArticleTable.Cols.ID + "=?", new String[]{article.getId()});
        db.close();
        return update != 0;
    }

    private List<Article> getMatchArticles(Context context, String keyword) {
        List<Article> articleList = new ArrayList<>();
        SQLiteDatabase db = new ArticleDatabaseHelper(context).getReadableDatabase();

        String sql = "select * from " + ArticleTable.NAME + " where " + ArticleTable.Cols.DESC + " like '%" + keyword + "%'";
        String[] args = new String[] {"%" + keyword + "%"};
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.getCount() > 0) {
            ArticleCursorWrapper cursorWrapper = new ArticleCursorWrapper(cursor);
            while (cursor.moveToNext()) {
                Article article = cursorWrapper.getArticle();
                articleList.add(article);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return articleList;
    }

    /**
     * ContentValues 是一个键值对存储类，但是它能保存的数据类型取决于SQLite 所能保存的数据类型
     *
     * @param item Article
     * @return ContentValues
     */
    private ContentValues getContentValues(Article item) {
        ContentValues values = new ContentValues();
        values.put(ArticleTable.Cols.ID, item.getId());
        values.put(ArticleTable.Cols.DESC, item.getDesc());
        values.put(ArticleTable.Cols.IMAGE_URL, StringUtil.changeListToString(item.getImageUrls()));
        values.put(ArticleTable.Cols.TYPE, item.getType().toString());
        values.put(ArticleTable.Cols.URL, item.getUrl());
        values.put(ArticleTable.Cols.PUBLISHED_TIME, item.getPublishedTime());
        values.put(ArticleTable.Cols.STARRED, item.isStarred() ? 1 : 0);

        return values;
    }
}
