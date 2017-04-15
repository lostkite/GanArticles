package com.cambrian.android.ganarticles.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cambrian.android.ganarticles.db.ArticleDbSchema.ArticleTable;

/**
 * 数据库连接类
 *
 * Created by S.J.Xiong on 2017/3/10.
 */

public class ArticleDatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "article.db";

    public ArticleDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ArticleTable.NAME + "(" +
                "_id integer primary key autoincrement," +
                ArticleTable.Cols.ID + "," +
                ArticleTable.Cols.DESC + " TEXT NOT NULL, " +
                ArticleTable.Cols.IMAGE_URL + "," +
                ArticleTable.Cols.TYPE + "," +
                ArticleTable.Cols.URL + "," +
                ArticleTable.Cols.PUBLISHED_TIME + "," +
                ArticleTable.Cols.STARRED +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
