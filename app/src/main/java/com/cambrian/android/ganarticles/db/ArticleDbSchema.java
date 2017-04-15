package com.cambrian.android.ganarticles.db;

/**
 * db row_name
 * Created by S.J.Xiong on 2017/3/9.
 */

public class ArticleDbSchema {
    public static final class ArticleTable {
        public static final String NAME = "articles";

        public static final class Cols {
            public static final String ID = "id";
            public static final String DESC = "desc";
            public static final String IMAGE_URL = "image_url";
            public static final String TYPE = "type";
            public static final String URL = "url";
            public static final String PUBLISHED_TIME = "published_time";
            public static final String STARRED = "starred";
        }
    }

}
