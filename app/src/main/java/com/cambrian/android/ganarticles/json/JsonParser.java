package com.cambrian.android.ganarticles.json;

import com.cambrian.android.ganarticles.enties.Article;
import com.cambrian.android.ganarticles.enties.TypeEnum;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 将 json 数据转换成 {@link com.cambrian.android.ganarticles.enties.Article} list
 * Created on 2017/3/3.
 */

public class JsonParser {
    /**
     * 转换按类别获取的文章
     *
     * @param articles {@link Article}
     * @param jsonString 网络请求返回的结果
     * @throws JSONException json 异常在调用时处理
     */
    public static void parseCategoryItems(List<Article> articles, String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        if (jsonObject.has(JsonSchema.RESULT)) {
            JSONArray articleJsonArray = jsonObject.getJSONArray(JsonSchema.RESULT);

            new JsonParser().parseJsonArray(JsonSchema.ID, articles, articleJsonArray);
        }
    }

    /**
     * 转换搜索返回的结果
     *
     * @param articles {@link Article}
     * @param jsonString 网络请求返回结果
     * @throws JSONException 调用时处理
     */
    public static void parseSearchItems(List<Article> articles, String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        if (jsonObject.has(JsonSchema.RESULT)) {
            JSONArray articleJsonArray = jsonObject.getJSONArray(JsonSchema.RESULT);

            new JsonParser().parseJsonArray(JsonSchema.GAN_ID, articles, articleJsonArray);
        }
    }

    /**
     * 解析当天的JsonString
     *
     * @param articles {@link Article}
     * @param dailyJsonString 网络请求返回结果
     * @throws JSONException 调用时处理
     */
    public static void parseDailyItems(List<Article> articles, String dailyJsonString) throws JSONException {
        JSONObject jsonBody = new JSONObject(dailyJsonString);
        // 获取当天干货的所有类型
        JSONArray categoryJsonArray = jsonBody.getJSONArray(JsonSchema.CATEGORY);
        JSONObject resultsJsonObject = jsonBody.getJSONObject(JsonSchema.RESULT);

        for (int i = 0; i < categoryJsonArray.length(); i++) {
            // 干货类型
            String category = categoryJsonArray.getString(i);
            // 相应的数组
            JSONArray jsonArray = resultsJsonObject.getJSONArray(category);

            new JsonParser().parseJsonArray(JsonSchema.ID, articles, jsonArray);
        }
    }

    /**
     * 将 JSONArray 转换为 article 的 list
     *
     * @param idKey Json 结果中的id
     * @param articles article
     * @param jsonArray jsonArray
     * @throws JSONException json异常
     */
    private void parseJsonArray(String idKey, List<Article> articles, JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject articleJsonObject = jsonArray.getJSONObject(i);

            if (!articleJsonObject.has(JsonSchema.URL)) {
                continue;
            }
            String id = articleJsonObject.getString(idKey);
            String desc = articleJsonObject.getString(JsonSchema.DESC);
            TypeEnum type = TypeEnum.valueOf(articleJsonObject.getString(JsonSchema.TYPE));
            String url = articleJsonObject.getString(JsonSchema.URL);

            String publishedAt = articleJsonObject.getString(JsonSchema.PUBLISHED_AT);

            List<String> imageUrls = new ArrayList<>();
            if (articleJsonObject.has(JsonSchema.IMAGES)) {
                JSONArray imageUrlsJsonArray = articleJsonObject.getJSONArray(JsonSchema.IMAGES);
                for (int j = 0; j < imageUrlsJsonArray.length(); j++) {
                    imageUrls.add(imageUrlsJsonArray.getString(j));
                }
            }

            Article article = new Article.Builder(id, desc, type, url)
                    .publishedTime(publishedAt)
                    .imageUrls(imageUrls)
                    .build();

            articles.add(article);
        }
    }
}
