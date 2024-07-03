package utils;

import models.Brand;
import models.Category;
import models.Customer;
import models.Order;
import models.Product;
import utils.structure.ArbolBinario;
import views.OrderPage;
import views.ProductPage;
import views.dialog.CreateOrderForm;
import views.dialog.EditOrderForm;

public class GlobalCacheState {

    final static protected ArbolBinario<Order> orders = new ArbolBinario();
    final static protected ArbolBinario<Customer> customers = new ArbolBinario();
    final static protected ArbolBinario<Product> products = new ArbolBinario();
    final static protected ArbolBinario<Category> categories = new ArbolBinario();
    final static protected ArbolBinario<Brand> brands = new ArbolBinario();

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

    static public void syncOrders() {
        OrderPage.syncOrders();
    }

    static public void syncProducts() {
        CreateOrderForm.syncProducts();
        EditOrderForm.syncProducts();
        ProductPage.syncProducts();
    }

    static public void syncCustomers() {
        CreateOrderForm.syncCustomers();
        EditOrderForm.syncCustomers();
    }

    static public void syncCategories() {

    }

    static public void syncBrands() {

    }
}
