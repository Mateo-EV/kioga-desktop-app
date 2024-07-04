package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Brand;
import utils.ApiClient;
import utils.BackgroundSwingWorker;
import utils.GlobalCacheState;
import utils.structure.ArbolBinario;

public class BrandController implements ModelController<Brand> {

    private static final BrandController instance = new BrandController();

    public static BrandController getInstance() {
        return instance;
    }

    static public Brand transcriptBrand(Map<String, Object> brandMap) {
        Brand brand = new Brand(
            ((Number) brandMap.get("id")).intValue(),
            (String) brandMap.get("name"),
            (String) brandMap.get("slug"),
            (String) brandMap.get("image")
        );
        brand.setCreatedAtFromTimeStamp((String) brandMap.get(
            "created_at"));
        brand.setUpdatedAtFromTimeStamp((String) brandMap.get(
            "updated_at"));

        return brand;
    }

    @Override
    public void findById(int id, ApiClient.onResponse onResponse) {
        Brand brandCached = GlobalCacheState.getBrands().find(id);
        if (brandCached != null) {
            onResponse.onSuccess(new ApiClient.ApiResponse(
                ApiClient.ResponseType.SUCCESS,
                null,
                brandCached
            ));
            return;
        }

        new BackgroundSwingWorker(
            "/admin/brands/" + id,
            "GET",
            null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Map<String, Object> brandsMap = (Map<String, Object>) apiResponse.getData();

                Brand brand = transcriptBrand(brandsMap);
                GlobalCacheState.getBrands().update(brand);

                onResponse.onSuccess(new ApiClient.ApiResponse(
                    ApiClient.ResponseType.SUCCESS,
                    null,
                    brand
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
        if (!GlobalCacheState.getBrands().isEmpty()) {
            onResponse.onSuccess(new ApiClient.ApiResponse(
                ApiClient.ResponseType.SUCCESS,
                null,
                GlobalCacheState.getBrands()
            ));
            return;
        }

        new BackgroundSwingWorker(
            "/admin/brands",
            "GET",
            null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                List<Map<String, Object>> brandsMap = (List<Map<String, Object>>) apiResponse.getData();
                ArbolBinario<Brand> brands = GlobalCacheState.getBrands();
                for (Map<String, Object> brandMap : brandsMap) {
                    brands.insert(transcriptBrand(brandMap));
                }
                onResponse.onSuccess(new ApiClient.ApiResponse(
                    ApiClient.ResponseType.SUCCESS,
                    null,
                    brands
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
    public void save(Brand brand, ApiClient.onResponse onResponse) {
        Map<String, Object> brandMap = new HashMap<>();
        brandMap.put("name", brand.getName());
        brandMap.put("image", brand.getImageFile());

        new BackgroundSwingWorker("/admin/brands", "POST", brandMap,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Map<String, Object> brandMap = (Map<String, Object>) apiResponse.getData();

                GlobalCacheState.getBrands().insert(transcriptBrand(
                    brandMap));
                GlobalCacheState.syncBrands();

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
    public void update(Brand brand, ApiClient.onResponse onResponse) {
        Map<String, Object> brandMap = new HashMap<>();
        brandMap.put("name", brand.getName());

        if (brand.getImageFile() != null) {
            brandMap.put("image", brand.getImageFile());
        }

        new BackgroundSwingWorker("/admin/brands/update/" + brand.getId(),
            "POST",
            brandMap,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Map<String, Object> brandMap = (Map<String, Object>) apiResponse.getData();

                GlobalCacheState.getBrands().update(transcriptBrand(
                    brandMap));
                GlobalCacheState.syncBrands();

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
        new BackgroundSwingWorker("/admin/brands/" + id, "DELETE",
            null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                GlobalCacheState.getBrands().delete(id);
                GlobalCacheState.syncBrands();

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
