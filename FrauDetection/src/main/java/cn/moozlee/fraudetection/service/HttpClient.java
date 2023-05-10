package cn.moozlee.fraudetection.service;

import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class HttpClient {

    private final OkHttpClient client;

    public HttpClient() {
        client = new OkHttpClient.Builder().build();
    }

    public void postFormDataAsync(String url, Map<String, String> formData, Callback callback) {
        // 创建请求体对象
        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (String key : formData.keySet()) {
            requestBodyBuilder.addFormDataPart(key, formData.get(key));
        }
        RequestBody requestBody = requestBodyBuilder.build();

        // 创建POST请求对象
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // 发送异步请求
        client.newCall(request).enqueue(callback);
    }

    public Response postFormData(String url, Map<String, String> formData) throws IOException {
        // 创建请求体对象
        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (String key : formData.keySet()) {
            requestBodyBuilder.addFormDataPart(key, formData.get(key));
        }
        RequestBody requestBody = requestBodyBuilder.build();

        // 创建POST请求对象
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // 发送异步请求
        return client.newCall(request).execute();
    }

}

