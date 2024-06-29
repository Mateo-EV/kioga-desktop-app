package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import models.Order;
import models.Sale;
import models.Customer;
import models.OrderStatus;
import utils.ApiClient;
import utils.BackgroundSwingWorker;
import utils.structure.Cola;

public class OrderController implements ModelController<Order> {
    static public OrderController instance = new OrderController();

    @Override
    public void findById(int id, ApiClient.onResponse onResponse) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void findAll(ApiClient.onResponse onResponse) {
        new BackgroundSwingWorker(
            "/admin/orders", 
            "GET",
            null,
            new ApiClient.onResponse() {
                @Override
                public void onSuccess(ApiClient.ApiResponse apiResponse) {
                    List<Map<String, Object>> ordersMap = (List<Map<String, Object>>) apiResponse.getData();
                    List<Order> orders = new ArrayList();
                    for (Map<String, Object> orderMap : ordersMap) {
                        Order order = new Order();
                        order.setId(((Number) orderMap.get("id")).intValue());
                        order.setCode((String) orderMap.get("code"));
                        order.setAmount((double) orderMap.get("amount"));
                        order.setStatus(OrderStatus.fromString((String) orderMap.get("status")));
                        order.setShippingAmount((double) orderMap.get("shipping_amount"));
                        order.setNotes((String) orderMap.get("notes"));
                        order.setCreatedAtFromTimeStamp(
                                (String) orderMap.get("created_at")
                        );
                        order.setUpdatedAtFromTimeStamp(
                                (String) orderMap.get("updated_at")
                        );
                        orders.add(order);
                    }
                    onResponse.onSuccess(new ApiClient.ApiResponse(
                        ApiClient.ResponseType.SUCCESS,
                        null,
                        orders
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
    public void save(Order order, ApiClient.onResponse onResponse) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(Order order, ApiClient.onResponse onResponse) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(Order order, String field, ApiClient.onResponse onResponse) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete(int id, ApiClient.onResponse onResponse) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void getDataToSave(ApiClient.onResponse onResponse) {
        String endpoints[] = new String[]{"/admin/users", "/admin/products"};
        String methods[] = new String[]{"GET", "GET"};
        boolean expectList[] = new boolean[]{true, true};
        new BackgroundSwingWorker(
                endpoints,
                methods, null, new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                List<Map<String, Object>>[] response = (List[]) apiResponse.getData();
                List<Map<String, Object>> usersMap = response[0];
                List<Map<String, Object>> productsMap = response[0];

            }

            @Override
            public void onError(ApiClient.ApiResponse apiResponse) {
                onResponse.onError(apiResponse);
            }
        }, expectList).execute();
    }
    
    public Cola<Order> getOrdersToDelivery(){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public List<Sale> getSales(){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public List<Order> findByDate(Date start_date, Date end_date) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public List<Order> findBetweenDates(Date fecha) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Order> findOrdersByCustomer(Customer customer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
