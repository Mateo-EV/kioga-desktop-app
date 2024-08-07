/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Customer;
import utils.ApiClient;
import utils.BackgroundSwingWorker;
import utils.GlobalCacheState;
import utils.structure.ArbolBinario;

/**
 *
 * @author intel
 */
public class CustomerController implements ModelController<Customer> {

    private static final CustomerController instance = new CustomerController();

    public static CustomerController getInstance() {
        return instance;
    }

    static public Customer transcriptCustomer(Map<String, Object> customerMap) {
        Customer customer = new Customer(
            ((Number) customerMap.get("id")).intValue(),
            (String) customerMap.get("name"),
            (String) customerMap.get("email")
        );
        customer.setCreatedAtFromTimeStamp((String) customerMap.get(
            "created_at"));
        customer.setUpdatedAtFromTimeStamp((String) customerMap.get(
            "updated_at"));

        return customer;
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
            "/admin/users/" + id,
            "GET",
            null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Map<String, Object> categoryMap = (Map<String, Object>) apiResponse.getData();

                Customer customer = transcriptCustomer(categoryMap);

                GlobalCacheState.getCustomers().update(customer);

                onResponse.onSuccess(new ApiClient.ApiResponse(
                    ApiClient.ResponseType.SUCCESS,
                    null,
                    customer
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
        if (!GlobalCacheState.getCustomers().isEmpty()) {
            onResponse.onSuccess(new ApiClient.ApiResponse(
                ApiClient.ResponseType.SUCCESS,
                null,
                GlobalCacheState.getCategories()
            ));
            return;
        }

        new BackgroundSwingWorker(
            "/admin/users",
            "GET",
            null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                List<Map<String, Object>> customersMap = (List<Map<String, Object>>) apiResponse.getData();
                ArbolBinario<Customer> customers = GlobalCacheState.getCustomers();
                for (Map<String, Object> customerMap : customersMap) {
                    customers.insert(transcriptCustomer(customerMap));
                }
                onResponse.onSuccess(new ApiClient.ApiResponse(
                    ApiClient.ResponseType.SUCCESS,
                    null,
                    customers
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
    public void save(Customer customer, ApiClient.onResponse onResponse) {
        Map<String, Object> customerMap = new HashMap();
        customerMap.put("name", customer.getName());
        customerMap.put("email", customer.getEmail());
        customerMap.put("password", customer.getPassword());

        new BackgroundSwingWorker("/admin/users", "POST", customerMap,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Map<String, Object> categoryMap = (Map<String, Object>) apiResponse.getData();

                GlobalCacheState.getCustomers().insert(transcriptCustomer(
                    categoryMap));
                GlobalCacheState.syncCustomers();

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
    public void update(Customer customer, ApiClient.onResponse onResponse) {
        Map<String, Object> customerMap = new HashMap<>();
        customerMap.put("name", customer.getName());
        customerMap.put("email", customer.getEmail());
        customerMap.put("password", customer.getPassword());

        new BackgroundSwingWorker("/admin/users/" + customer.getId(),
            "PUT",
            customerMap,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Map<String, Object> categoryMap = (Map<String, Object>) apiResponse.getData();

                GlobalCacheState.getCustomers().update(transcriptCustomer(
                    categoryMap));
                GlobalCacheState.syncCustomers();

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
        new BackgroundSwingWorker("/admin/users/" + id, "DELETE",
            null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                GlobalCacheState.getCustomers().delete(id);
                GlobalCacheState.syncCustomers();

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
