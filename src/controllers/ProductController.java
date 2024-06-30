package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Brand;
import models.Category;
import models.Product;
import utils.ApiClient;
import utils.BackgroundSwingWorker;

public class ProductController implements ModelController<Product> {

    static public List<Product> products = new ArrayList();
    private static final ProductController instance = new ProductController();

    static public ProductController getInstance() {
        return instance;
    }

    @Override
    public void findById(int id, ApiClient.onResponse onResponse) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void findAll(ApiClient.onResponse onResponse) {
        new BackgroundSwingWorker(
            "/admin/products",
            "GET",
            null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                List<Map<String, Object>> productsMap = (List<Map<String, Object>>) apiResponse.getData();
                List<Product> products = new ArrayList();
                for (Map<String, Object> productMap : productsMap) {
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

                    products.add(product);
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

    @Override
    public void save(Product product, ApiClient.onResponse onResponse) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(Product product, ApiClient.onResponse onResponse) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(Product product, String field,
        ApiClient.onResponse onResponse) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete(int id, ApiClient.onResponse onResponse) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public List<Product> findByCategoria(Category category) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Product> findByBrand(Brand brand) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
