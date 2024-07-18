/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Admin;
import models.Customer;
import utils.ApiClient;
import utils.BackgroundSwingWorker;
import utils.GlobalCacheState;
import utils.structure.ArbolBinario;

/**
 *
 * @author intel
 */
public class AdminController implements ModelController<Admin> {

    private static final AdminController instance = new AdminController();

    public static AdminController getInstance() {
        return instance;
    }

    static public Admin transcriptAdmin(Map<String, Object> customerMap) {
        Admin admin = new Admin(
            ((Number) customerMap.get("id")).intValue(),
            (String) customerMap.get("name"),
            (String) customerMap.get("email")
        );
        admin.setCreatedAtFromTimeStamp((String) customerMap.get(
            "created_at"));
        admin.setUpdatedAtFromTimeStamp((String) customerMap.get(
            "updated_at"));

        return admin;
    }

    @Override
    public void findById(int id, ApiClient.onResponse onResponse) {
        Customer categoryCached = GlobalCacheState.getCustomers().find(id);
        if (categoryCached != null) {
            onResponse.onSuccess(new ApiClient.ApiResponse(
                ApiClient.ResponseType.SUCCESS,
                null,
                categoryCached
            ));
            return;
        }

        new BackgroundSwingWorker(
            "/admin/manage/" + id,
            "GET",
            null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Map<String, Object> categoryMap = (Map<String, Object>) apiResponse.getData();

                Admin admin = transcriptAdmin(categoryMap);

                GlobalCacheState.getAdmins().update(admin);

                onResponse.onSuccess(new ApiClient.ApiResponse(
                    ApiClient.ResponseType.SUCCESS,
                    null,
                    admin
                ));
            }

            @Override
            public void onError(ApiClient.ApiResponse apiResponse) {
                onResponse.onError(apiResponse);
            }
        },
            false
        ).execute();
    }

    @Override
    public void findAll(ApiClient.onResponse onResponse) {
        if (!GlobalCacheState.getAdmins().isEmpty()) {
            onResponse.onSuccess(new ApiClient.ApiResponse(
                ApiClient.ResponseType.SUCCESS,
                null,
                GlobalCacheState.getAdmins()
            ));
            return;
        }

        new BackgroundSwingWorker(
            "/admin/manage",
            "GET",
            null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                List<Map<String, Object>> adminsMap = (List<Map<String, Object>>) apiResponse.getData();
                ArbolBinario<Admin> admins = GlobalCacheState.getAdmins();
                for (Map<String, Object> adminMap : adminsMap) {
                    admins.insert(transcriptAdmin(adminMap));
                }
                onResponse.onSuccess(new ApiClient.ApiResponse(
                    ApiClient.ResponseType.SUCCESS,
                    null,
                    admins
                ));
            }

            @Override
            public void onError(ApiClient.ApiResponse apiResponse) {
                onResponse.onError(apiResponse);
            }
        },
            true
        ).execute();
    }

    @Override
    public void save(Admin customer, ApiClient.onResponse onResponse) {
        Map<String, Object> customerMap = new HashMap();
        customerMap.put("name", customer.getName());
        customerMap.put("email", customer.getEmail());
        customerMap.put("password", customer.getPassword());

        new BackgroundSwingWorker("/admin/manage", "POST", customerMap,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Map<String, Object> categoryMap = (Map<String, Object>) apiResponse.getData();

                GlobalCacheState.getAdmins().insert(transcriptAdmin(
                    categoryMap));
                GlobalCacheState.syncAdmins();

                onResponse.onSuccess(new ApiClient.ApiResponse(
                    ApiClient.ResponseType.SUCCESS,
                    null,
                    null
                ));
            }

            @Override
            public void onError(ApiClient.ApiResponse apiResponse) {
                onResponse.onError(apiResponse);
            }
        }, false).execute();
    }

    @Override
    public void update(Admin customer, ApiClient.onResponse onResponse) {
        Map<String, Object> customerMap = new HashMap<>();
        customerMap.put("name", customer.getName());
        customerMap.put("email", customer.getEmail());
        customerMap.put("password", customer.getPassword());

        new BackgroundSwingWorker("/admin/manage/" + customer.getId(),
            "PUT",
            customerMap,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Map<String, Object> categoryMap = (Map<String, Object>) apiResponse.getData();

                GlobalCacheState.getAdmins().update(transcriptAdmin(
                    categoryMap));
                GlobalCacheState.syncAdmins();

                onResponse.onSuccess(new ApiClient.ApiResponse(
                    ApiClient.ResponseType.SUCCESS,
                    null,
                    null
                ));
            }

            @Override
            public void onError(ApiClient.ApiResponse apiResponse) {
                onResponse.onError(apiResponse);
            }
        }, false).execute();
    }

    @Override
    public void delete(int id, ApiClient.onResponse onResponse) {
        new BackgroundSwingWorker("/admin/manage/" + id, "DELETE",
            null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                GlobalCacheState.getAdmins().delete(id);
                GlobalCacheState.syncAdmins();

                onResponse.onSuccess(new ApiClient.ApiResponse(
                    ApiClient.ResponseType.SUCCESS,
                    null,
                    null
                ));
            }

            @Override
            public void onError(ApiClient.ApiResponse apiResponse) {
                onResponse.onError(apiResponse);
            }
        }, false).execute();
    }
}
