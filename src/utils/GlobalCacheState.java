package utils;

import java.util.ArrayList;
import java.util.List;
import models.Customer;
import models.Order;
import models.Product;
import views.OrderPage;
import views.ProductPage;
import views.dialog.CreateOrderForm;

public class GlobalCacheState {

    final static protected List<Order> orders = new ArrayList();
    final static protected List<Customer> customers = new ArrayList();
    final static protected List<Product> products = new ArrayList();

    public static List<Order> getOrders() {
        return orders;
    }

    public static List<Product> getProducts() {
        return products;
    }

    public static List<Customer> getCustomers() {
        return customers;
    }

    static public void syncOrders() {
        OrderPage.syncOrders();
    }

    static public void syncProducts() {
        CreateOrderForm.syncProducts();
        ProductPage.syncProducts();
    }

    static public void syncCustomers() {
        CreateOrderForm.syncProducts();
    }
}
