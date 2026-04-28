package com.example.demo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

/**
 * 简单 HTTP 工具类（基于 JDK8 HttpURLConnection）。
 */
public final class HttpUtils {

    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;
    private static final int DEFAULT_READ_TIMEOUT = 10000;

    private HttpUtils() {
        // utility class
    }

    public static String get(String url) throws IOException {
        return get(url, Collections.<String, String>emptyMap());
    }

    public static String get(String url, Map<String, String> headers) throws IOException {
        HttpURLConnection connection = openConnection(url, "GET", headers);
        return execute(connection, null);
    }

    public static String postJson(String url, String jsonBody) throws IOException {
        return postJson(url, jsonBody, Collections.<String, String>emptyMap());
    }

    public static String postJson(String url, String jsonBody, Map<String, String> headers) throws IOException {
        HttpURLConnection connection = openConnection(url, "POST", headers);
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        return execute(connection, jsonBody == null ? "" : jsonBody);
    }

    private static HttpURLConnection openConnection(String targetUrl, String method,
                                                    Map<String, String> headers) throws IOException {
        URL url = new URL(targetUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_READ_TIMEOUT);
        connection.setUseCaches(false);

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
        }
        return connection;
    }

    private static String execute(HttpURLConnection connection, String body) throws IOException {
        try {
            if (body != null) {
                connection.setDoOutput(true);
                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                connection.setRequestProperty("Content-Length", String.valueOf(bytes.length));
                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(bytes);
                    outputStream.flush();
                }
            }

            int statusCode = connection.getResponseCode();
            InputStream inputStream = statusCode >= HttpURLConnection.HTTP_BAD_REQUEST
                    ? connection.getErrorStream() : connection.getInputStream();
            String responseBody = readAll(inputStream);

            if (statusCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
                throw new IOException("HTTP request failed, status=" + statusCode + ", body=" + responseBody);
            }
            return responseBody;
        } finally {
            connection.disconnect();
        }
    }

    private static String readAll(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
