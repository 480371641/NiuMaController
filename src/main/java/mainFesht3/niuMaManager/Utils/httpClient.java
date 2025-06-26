package mainFesht3.niuMaManager.Utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class httpClient {
    private String baseUrl;

    public httpClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // 重载方法：支持无参数的GET请求
    public String get() {
        return get(new HashMap<>());
    }

    // 支持带查询参数的GET请求
    public String get(Map<String, String> queryParams) {
        try {
            // 构建完整URL（包含查询参数）
            String fullUrl = buildUrlWithQueryParams(baseUrl, queryParams);
            URL url = new URL(fullUrl);

            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法
            connection.setRequestMethod("GET");

            // 添加请求头
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            // 获取响应码
            int responseCode = connection.getResponseCode();
            System.out.println("响应码: " + responseCode);

            // 读取响应数据
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            String res = response.toString();
            System.out.println("响应数据: " + res);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // 返回null而不是"null"字符串
        }
    }

    // 辅助方法：构建带查询参数的URL
    private String buildUrlWithQueryParams(String baseUrl, Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return baseUrl;
        }

        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> param : queryParams.entrySet()) {
            if (queryString.length() > 0) {
                queryString.append("&");
            }
            try {
                queryString.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8));
                queryString.append("=");
                queryString.append(URLEncoder.encode(param.getValue(), StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
                // 如果编码出错，添加原始参数（不推荐，但比忽略好）
                queryString.append(param.getKey()).append("=").append(param.getValue());
            }
        }

        // 处理基础URL中是否已包含?符号
        if (baseUrl.contains("?")) {
            return baseUrl + "&" + queryString;
        } else {
            return baseUrl + "?" + queryString;
        }
    }
}