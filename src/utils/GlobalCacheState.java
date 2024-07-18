package utils;

import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import models.Admin;
import models.Brand;
import models.Category;
import models.Customer;
import models.Order;
import models.Product;
import utils.structure.ArbolBinario;
import views.BrandPage;
import views.CategoryPage;
import views.CustomerPage;
import views.OrderPage;
import views.ProductPage;
import views.dialog.CreateOrderForm;

public class GlobalCacheState {

    final static protected ArbolBinario<Order> orders = new ArbolBinario();
    final static protected ArbolBinario<Customer> customers = new ArbolBinario();
    final static protected ArbolBinario<Product> products = new ArbolBinario();
    final static protected ArbolBinario<Category> categories = new ArbolBinario();
    final static protected ArbolBinario<Brand> brands = new ArbolBinario();
    final static protected ArbolBinario<Admin> admins = new ArbolBinario();

    final static protected Map<String, ImageIcon> images = new HashMap<>();

    public static ArbolBinario<Order> getOrders() {
        return orders;
    }

    public static ArbolBinario<Product> getProducts() {
        return products;
    }

    public static ArbolBinario<Customer> getCustomers() {
        return customers;
    }

    public static ArbolBinario<Category> getCategories() {
        return categories;
    }

    public static ArbolBinario<Brand> getBrands() {
        return brands;
    }

    public static ArbolBinario<Admin> getAdmins() {
        return admins;
    }

    public static Map<String, ImageIcon> getImages() {
        return images;
    }

    static public void syncOrders() {
        OrderPage.syncOrders();
    }

    static public void syncProducts() {
        CreateOrderForm.syncProducts();
        ProductPage.syncProducts();
    }

    static public void syncCustomers() {
        CreateOrderForm.syncCustomers();
        CustomerPage.syncCustomers();
    }

    static public void syncCategories() {
        CategoryPage.syncCategories();
    }

    static public void syncBrands() {
        BrandPage.syncBrands();
    }

    static public void syncAdmins() {

    }
}
