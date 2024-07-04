/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Category;
import models.Subcategory;
import utils.ApiClient;
import utils.BackgroundSwingWorker;
import utils.GlobalCacheState;
import utils.structure.ArbolBinario;

/**
 *
 * @author intel
 */
public class CategoryController implements ModelController<Category> {

    private static final CategoryController instance = new CategoryController();

    public static CategoryController getInstance() {
        return instance;
    }

    static public Category transcriptCategory(Map<String, Object> categoryMap) {
        Category category = new Category(
            ((Number) categoryMap.get("id")).intValue(),
            (String) categoryMap.get("name"),
            (String) categoryMap.get("slug"),
            (String) categoryMap.get("image")
        );
        category.setCreatedAtFromTimeStamp((String) categoryMap.get(
            "created_at"));
        category.setUpdatedAtFromTimeStamp((String) categoryMap.get(
            "updated_at"));

        return category;
    }

    static public Subcategory transcriptSubcategory(
        Map<String, Object> subcategoryMap, Category category) {
        Subcategory customer = new Subcategory(
            ((Number) subcategoryMap.get("id")).intValue(),
            (String) subcategoryMap.get("name"),
            (String) subcategoryMap.get("slug"),
            category
        );
        customer.setCreatedAtFromTimeStamp((String) subcategoryMap.get(
            "created_at"));
        customer.setUpdatedAtFromTimeStamp((String) subcategoryMap.get(
            "updated_at"));

        return customer;
    }

    @Override
    public void findById(int id, ApiClient.onResponse onResponse) {
        Category categoryCached = GlobalCacheState.getCategories().find(id);
        if (categoryCached != null) {
            onResponse.onSuccess(new ApiClient.ApiResponse(
                ApiClient.ResponseType.SUCCESS,
                null,
                categoryCached
            ));
            return;
        }

        new BackgroundSwingWorker(
            "/admin/categories/" + id,
            "GET",
            null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Map<String, Object> categoryMap = (Map<String, Object>) apiResponse.getData();

                Category category = transcriptCategory(categoryMap);
                List<Map<String, Object>> subcategoriesMap
                    = (List<Map<String, Object>>) categoryMap.get(
                        "subcategories");
                for (Map<String, Object> subcategoryMap : subcategoriesMap) {
                    Subcategory subcategory = transcriptSubcategory(
                        subcategoryMap, category);
                    category.getSubcategories().add(subcategory);
                }
                GlobalCacheState.getCategories().update(category);

                onResponse.onSuccess(new ApiClient.ApiResponse(
                    ApiClient.ResponseType.SUCCESS,
                    null,
                    category
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
        if (!GlobalCacheState.getCategories().isEmpty()) {
            onResponse.onSuccess(new ApiClient.ApiResponse(
                ApiClient.ResponseType.SUCCESS,
                null,
                GlobalCacheState.getCategories()
            ));
            return;
        }

        new BackgroundSwingWorker(
            "/admin/categories",
            "GET",
            null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                List<Map<String, Object>> categoriesMap = (List<Map<String, Object>>) apiResponse.getData();
                ArbolBinario<Category> categories = GlobalCacheState.getCategories();
                for (Map<String, Object> categoryMap : categoriesMap) {
                    Category category = transcriptCategory(categoryMap);
                    List<Map<String, Object>> subcategoriesMap
                        = (List<Map<String, Object>>) categoryMap.get(
                            "subcategories");
                    for (Map<String, Object> subcategoryMap : subcategoriesMap) {
                        Subcategory subcategory = transcriptSubcategory(
                            subcategoryMap, category);
                        category.getSubcategories().add(subcategory);
                    }
                    categories.insert(category);
                }
                onResponse.onSuccess(new ApiClient.ApiResponse(
                    ApiClient.ResponseType.SUCCESS,
                    null,
                    categories
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
    public void save(Category category, ApiClient.onResponse onResponse) {
        Map<String, Object> categoryMap = new HashMap<>();
        categoryMap.put("name", category.getName());
        categoryMap.put("image", category.getImageFile());

        new BackgroundSwingWorker("/admin/categories", "POST", categoryMap,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Map<String, Object> categoryMap = (Map<String, Object>) apiResponse.getData();

                GlobalCacheState.getCategories().insert(transcriptCategory(
                    categoryMap));
                GlobalCacheState.syncCategories();

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
    public void update(Category category, ApiClient.onResponse onResponse) {
        Map<String, Object> categoryMap = new HashMap<>();
        categoryMap.put("name", category.getName());
        if (category.getImageFile() != null) {
            categoryMap.put("image", category.getImageFile());
        }

        new BackgroundSwingWorker("/admin/categories/update/" + category.getId(),
            "POST",
            categoryMap,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Map<String, Object> categoryMap = (Map<String, Object>) apiResponse.getData();

                GlobalCacheState.getCategories().update(transcriptCategory(
                    categoryMap));
                GlobalCacheState.syncCategories();

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
        new BackgroundSwingWorker("/admin/categories/" + id, "DELETE",
            null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                GlobalCacheState.getCategories().delete(id);
                GlobalCacheState.syncCategories();

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
