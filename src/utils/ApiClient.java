package utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8000";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final MediaType JSON = MediaType.parse(
        "application/json; charset=utf-8");

    private static String token;

    public interface onResponse {

        void onSuccess(ApiResponse apiResponse);

        void onError(ApiResponse apiResponse);
    }

    public static void setToken(String newToken) {
        token = newToken;
    }

    public static boolean tokenExists() {
        return token != null && !token.isEmpty();
    }

    public static HashMap<String, Object> sendRequest(String endpoint,
        String method, Map<String, Object> data) throws ApiException, IOException {
        Request request;
        if (method.equalsIgnoreCase("GET")) {
            request = createRequest(endpoint);
        } else {
            RequestBody body = createRequestBody(data);
            request = createRequest(endpoint, method, body);
        }

        try (Response response = client.newCall(request).execute()) {
            String bodyString = response.body().string();
            System.out.println(bodyString);
            Type type = new TypeToken<HashMap<String, Object>>() {
            }.getType();
            if (!response.isSuccessful()) {
                Map<String, Object> errorFormatted = gson.fromJson(bodyString,
                    type);
                throw new ApiException(response.code(),
                    (String) errorFormatted.get("message"));
            }
            return gson.fromJson(bodyString, type);
        }
    }

    public static List<HashMap<String, Object>> sendRequestForList(
        String endpoint, String method, Map<String, Object> data) throws ApiException, IOException {
        Request request;
        if (method.equalsIgnoreCase("GET")) {
            request = createRequest(endpoint);
        } else {
            RequestBody body = createRequestBody(data);
            request = createRequest(endpoint, method, body);
        }
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body().string();
                throw new ApiException(response.code(), errorBody);
            }

            String responseData = response.body().string();
            Type type = new TypeToken<List<HashMap<String, Object>>>() {
            }.getType();
            return gson.fromJson(responseData, type);
        }
    }

    private static RequestBody createRequestBody(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            return RequestBody.create("", JSON);
        }

        boolean hasFile = data.values().stream().anyMatch(
            value -> value instanceof File);
        if (hasFile) {
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(
                MultipartBody.FORM);

            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (entry.getValue() instanceof File) {
                    File file = (File) entry.getValue();
                    builder.addFormDataPart(entry.getKey(), file.getName(),
                        RequestBody.create(file, MediaType.parse(
                            "application/octet-stream")));
                } else {
                    builder.addFormDataPart(entry.getKey(),
                        entry.getValue().toString());
                }
            }

            return builder.build();
        } else {
            String json = gson.toJson(data);
            return RequestBody.create(json, JSON);
        }
    }

    private static Request createRequest(String endpoint, String method,
        RequestBody body) {
        HttpUrl url = HttpUrl.parse(BASE_URL + endpoint);

        if (url == null) {
            throw new IllegalArgumentException(
                "Invalid URL: " + BASE_URL + endpoint);
        }

        Request.Builder builder = new Request.Builder()
            .url(BASE_URL + endpoint)
            .method(method, body)
            .addHeader("Accept", "application/json");

        if (token != null && !token.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + token);
        }

        return builder.build();
    }

    private static Request createRequest(String endpoint) {
        HttpUrl url = HttpUrl.parse(BASE_URL + endpoint);

        if (url == null) {
            throw new IllegalArgumentException(
                "Invalid URL: " + BASE_URL + endpoint);
        }

        Request.Builder builder = new Request.Builder()
            .url(BASE_URL + endpoint)
            .addHeader("Accept", "application/json");

        if (token != null && !token.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + token);
        }

        return builder.build();
    }

    public static class ApiException extends Exception {

        private int statusCode;
        private String errorBody;

        public ApiException(int statusCode, String errorBody) {
            super("HTTP error code: " + statusCode);
            this.statusCode = statusCode;
            this.errorBody = errorBody;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getErrorBody() {
            return errorBody;
        }
    }

    public enum ResponseType {
        SUCCESS,
        ERROR,
    }

    public static class ApiResponse {

        private final ResponseType type;
        private final String message;
        private Object data;

        public ApiResponse(ResponseType type, String message) {
            this.type = type;
            this.message = message;
        }

        public ApiResponse(ResponseType type, String message, Object data) {
            this.type = type;
            this.message = message;
            this.data = data;
        }

        public ResponseType getType() {
            return type;
        }

        public String getMessage() {
            return message;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }
}
