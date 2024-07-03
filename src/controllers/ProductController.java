package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Brand;
import models.Category;
import models.Product;
import models.Subcategory;
import utils.ApiClient;
import utils.BackgroundSwingWorker;
import utils.GlobalCacheState;
import utils.structure.ArbolBinario;

public class ProductController implements ModelController<Product> {

    static public List<Product> products = new ArrayList();
    private static final ProductController instance = new ProductController();

    static public Product transcriptProduct(Map<String, Object> productMap) {
        Product product = new Product();
        product.setId(((Number) productMap.get("id")).intValue());
        product.setName((String) productMap.get("name"));
        product.setImage((String) productMap.get("image"));
        product.setSlug((String) productMap.get("slug"));
        product.setDescription(
            (String) productMap.get("description"));
        product.setPrice((double) productMap.get("price"));
        product.setDiscount((double) productMap.get("discount"));
        product.setPriceDiscounted((double) productMap.get(
            "price_discounted"));
        product.setStock(
            ((Number) productMap.get("stock")).intValue());
        product.setIsActive((boolean) productMap.get("is_active"));
        product.setCreatedAtFromTimeStamp((String) productMap.get(
            "created_at"));
        product.setUpdatedAtFromTimeStamp((String) productMap.get(
            "updated_at"));
        return product;
    }

    static public ProductController getInstance() {
        return instance;
    }

    @Override
    public void findById(int id, ApiClient.onResponse onResponse) {
        Product productCached = GlobalCacheState.getProducts().find(id);
        if (productCached != null
            && productCached.getCategory() != null
            && productCached.getBrand() != null
            && productCached.getSubcategory() != null) {
            onResponse.onSuccess(new ApiClient.ApiResponse(
                ApiClient.ResponseType.SUCCESS,
                null,
                productCached
            ));
        }

        new BackgroundSwingWorker(
            "/admin/products/" + id,
            "GET",
            null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Map<String, Object> productMap = (Map<String, Object>) apiResponse.getData();

                Product product = transcriptProduct(productMap);

                Brand brand = BrandController.transcriptBrand(
                    (Map<String, Object>) productMap.get("brand"));

                Category category = CategoryController.transcriptCategory(
                    (Map<String, Object>) productMap.get("category")
                );

                Subcategory subcategory = CategoryController.transcriptSubcategory(
                    (Map<String, Object>) productMap.get("subcategory"),
                    category
                );

                product.setCategory(category);
                product.setBrand(brand);
                product.setSubcategory(subcategory);

                GlobalCacheState.getProducts().update(product);

                onResponse.onSuccess(new ApiClient.ApiResponse(
                    ApiClient.ResponseType.SUCCESS,
                    null,
                    product
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
        if (!GlobalCacheState.getProducts().isEmpty()) {
            onResponse.onSuccess(new ApiClient.ApiResponse(
                ApiClient.ResponseType.SUCCESS,
                null,
                GlobalCacheState.getProducts()
            ));
            return;
        }

        new BackgroundSwingWorker(
            "/admin/products",
            "GET",
            null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                List<Map<String, Object>> productsMap = (List<Map<String, Object>>) apiResponse.getData();
                ArbolBinario<Product> products = GlobalCacheState.getProducts();
                for (Map<String, Object> productMap : productsMap) {
                    Product product = transcriptProduct(productMap);
                    products.insert(product);
                }
                onResponse.onSuccess(new ApiClient.ApiResponse(
                    ApiClient.ResponseType.SUCCESS,
                    null,
                    products
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

    public void getDataToSave(ApiClient.onResponse onResponse) {
        if (!GlobalCacheState.getBrands().isEmpty() && !GlobalCacheState.getCategories().isEmpty()) {
            onResponse.onSuccess(new ApiClient.ApiResponse(
                ApiClient.ResponseType.SUCCESS,
                null,
                new Object[]{GlobalCacheState.getCategories(), GlobalCacheState.getBrands()}
            ));
            return;
        }

        String endpoints[] = new String[]{"/admin/categories", "/admin/brands"};
        String methods[] = new String[]{"GET", "GET"};
        boolean expectList[] = new boolean[]{true, true};
        new BackgroundSwingWorker(
            endpoints,
            methods,
            null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Object[] response = (Object[]) apiResponse.getData();
                List<Map<String, Object>> categoriesMap = (List<Map<String, Object>>) response[0];
                List<Map<String, Object>> brandsMap = (List<Map<String, Object>>) response[1];

                ArbolBinario<Category> categories = GlobalCacheState.getCategories();
                for (Map<String, Object> categoryMap : categoriesMap) {
                    Category category = CategoryController.transcriptCategory(
                        categoryMap);
                    List<Map<String, Object>> subcategoriesMap = (List<Map<String, Object>>) categoryMap.get(
                        "subcategories"
                    );
                    List<Subcategory> subcategories = category.getSubcategories();
                    for (Map<String, Object> subcategory : subcategoriesMap) {
                        subcategories.add(
                            CategoryController.transcriptSubcategory(subcategory,
                                category));
                    }
                    categories.insert(category);
                }

                ArbolBinario<Brand> brands = GlobalCacheState.getBrands();
                for (Map<String, Object> brandMap : brandsMap) {
                    Brand brand = BrandController.transcriptBrand(brandMap);
                    brands.insert(brand);
                }

                Object responses[] = {categories, brands};
                onResponse.onSuccess(new ApiClient.ApiResponse(
                    ApiClient.ResponseType.SUCCESS, null, responses));
            }

            @Override
            public void onError(ApiClient.ApiResponse apiResponse) {
                onResponse.onError(apiResponse);
            }
        },
            expectList).execute();
    }

    @Override
    public void save(Product product, ApiClient.onResponse onResponse) {
        Map<String, Object> productMap = new HashMap();
        productMap.put("name", product.getName());
        productMap.put("description", product.getDescription());
        productMap.put("price", product.getPrice());
        productMap.put("discount", product.getDiscount());
        productMap.put("stock", product.getStock());
        productMap.put("category_id", product.getCategory().getId());
        productMap.put("subcategory_id", product.getSubcategory().getId());
        productMap.put("is_active", product.getIsActive());
        productMap.put("image", product.getImageFile());
        productMap.put("brand_id", product.getBrand().getId());

        new BackgroundSwingWorker("/admin/products", "POST", productMap,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Map<String, Object> productMap = (Map<String, Object>) apiResponse.getData();

                GlobalCacheState.getProducts().insert(transcriptProduct(
                    productMap));
                GlobalCacheState.syncProducts();

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
    public void update(Product product, ApiClient.onResponse onResponse) {
        Map<String, Object> productMap = new HashMap();
        productMap.put("name", product.getName());
        productMap.put("description", product.getDescription());
        productMap.put("price", product.getPrice());
        productMap.put("discount", product.getDiscount());
        productMap.put("stock", product.getStock());
        productMap.put("category_id", product.getCategory().getId());
        productMap.put("subcategory_id", product.getSubcategory().getId());
        productMap.put("is_active", product.getIsActive());
        productMap.put("image", product.getImageFile());
        productMap.put("brand_id", product.getBrand().getId());

        new BackgroundSwingWorker("/admin/products/" + product.getId(), "PUT",
            productMap,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Map<String, Object> productMap = (Map<String, Object>) apiResponse.getData();

                GlobalCacheState.getProducts().update(transcriptProduct(
                    productMap));
                GlobalCacheState.syncProducts();

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
        new BackgroundSwingWorker("/admin/products/" + id, "DELETE",
            null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                GlobalCacheState.getProducts().delete(id);
                GlobalCacheState.syncProducts();

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

    public List<Product> findByCategoria(Category category) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Product> findByBrand(Brand brand) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
