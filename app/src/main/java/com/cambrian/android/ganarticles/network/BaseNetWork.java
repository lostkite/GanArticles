package com.cambrian.android.ganarticles.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络连接
 * Created on 2017/2/28.
 */

public class BaseNetWork {

    /**
     * 跟据传入的 urlString 建立网络连接，并将结果以String 返回
     *
     * @param urlSpec urlString
     * @return String 网络返回数据， 可能为 null
     * @throws IOException 数据
     */
    public String getUrlResultString(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            // 对返回码进行处理
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            stream = connection.getInputStream();
            if (stream != null) {
                result = readStream(stream);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return result;

    }

    private String readStream(InputStream stream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int readSize;
        while ((readSize = stream.read(buffer)) > -1) {
            out.write(buffer, 0, readSize);
        }

        out.close();

        return new String(out.toByteArray());
    }
}
