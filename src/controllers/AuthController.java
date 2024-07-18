package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import models.Admin;
import utils.ApiClient;
import utils.BackgroundSwingWorker;

public class AuthController {

    private static final String TOKEN_KEY = "authKiogaToken";
    private static final Preferences preferences = Preferences.userRoot().node(
        AuthController.class.getName());

    private static void saveToken(String token) {
        preferences.put(TOKEN_KEY, token);
    }

    private static String getToken() {
        return preferences.get(TOKEN_KEY, null);
    }

    private static void clearToken() {
        preferences.remove(TOKEN_KEY);
    }

    static public void login(
        String email,
        String password,
        ApiClient.onResponse onResponse
    ) {
        Map<String, Object> loginData = new HashMap();
        loginData.put("email", email);
        loginData.put("password", password);

        new BackgroundSwingWorker("/admin/login/desktop", "POST", loginData,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Map<String, Object> loginResponse = (Map<String, Object>) apiResponse.getData();
                String token = (String) loginResponse.get("token");
                ApiClient.setToken(token);
                saveToken(token);

                Admin admin = getSession();

                String successMessage = "Autenticación exitosa. Bienvenido!";
                ApiClient.ApiResponse customResponse = new ApiClient.ApiResponse(
                    ApiClient.ResponseType.SUCCESS,
                    successMessage,
                    admin
                );

                onResponse.onSuccess(customResponse);
            }

            @Override
            public void onError(ApiClient.ApiResponse apiResponse) {
                onResponse.onError(apiResponse);
            }
        }, false).execute();
    }

    static public Admin getSession() {
        if (!ApiClient.tokenExists()) {
            String token = getToken();
            System.out.println(token);
            if (token == null) {
                return null;
            }
            ApiClient.setToken(token);
        };

        try {
            Map<String, Object> admin_map = ApiClient.sendRequest("/api/admin",
                "GET", null);

            return AdminController.transcriptAdmin(admin_map);
        } catch (Exception ex) {
            return null;
        }
    }

    static public void logout() {
        ApiClient.setToken(null);
        clearToken();
    }
}
