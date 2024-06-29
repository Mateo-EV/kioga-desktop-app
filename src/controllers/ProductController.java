package controllers;

import java.util.List;
import models.Brand;
import models.Category;
import models.Product;
import utils.ApiClient;

public class ProductController implements ModelController<Product> {
    @Override
    public void findById(int id, ApiClient.onResponse onResponse) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void findAll(ApiClient.onResponse onResponse) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
    public void update(Product product, String field, ApiClient.onResponse onResponse) {
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
