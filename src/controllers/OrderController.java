package controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import models.Address;
import models.Customer;
import models.Order;
import models.OrderStatus;
import models.Product;
import models.Sale;
import utils.ApiClient;
import utils.BackgroundSwingWorker;
import utils.GlobalCacheState;
import utils.structure.Cola;

public class OrderController implements ModelController<Order> {

    static private OrderController instance = new OrderController();

    static public OrderController getInstance() {
        return instance;
    }

    static public Order transcriptOrder(Map<String, Object> orderMap) {
        Order order = new Order();
        order.setId(((Number) orderMap.get("id")).intValue());
        order.setCode((String) orderMap.get("code"));
        order.setAmount((double) orderMap.get("amount"));
        order.setStatus(OrderStatus.fromString(
            (String) orderMap.get("status")));
        order.setShippingAmount((double) orderMap.get(
            "shipping_amount"));
        order.setNotes((String) orderMap.get("notes"));
        order.setCreatedAtFromTimeStamp(
            (String) orderMap.get("created_at")
        );
        order.setUpdatedAtFromTimeStamp(
            (String) orderMap.get("updated_at")
        );

        return order;
    }

    @Override
    public void findById(int id,
        ApiClient.onResponse onResponse) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void findAll(ApiClient.onResponse onResponse) {
        if (!GlobalCacheState.getOrders().isEmpty()) {
            onResponse.onSuccess(new ApiClient.ApiResponse(
                ApiClient.ResponseType.SUCCESS,
                null,
                GlobalCacheState.getOrders()
            ));
            return;
        }

        new BackgroundSwingWorker(
            "/admin/orders",
            "GET",
            null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                List<Map<String, Object>> ordersMap = (List<Map<String, Object>>) apiResponse.getData();
                List<Order> orders = GlobalCacheState.getOrders();
                for (Map<String, Object> orderMap : ordersMap) {
                    orders.add(transcriptOrder(orderMap));
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
    public void save(Order order,
        ApiClient.onResponse onResponse) {

        Map<String, Object> data = new HashMap<>();
        data.put("user_id", order.getCustomer().getId());
        data.put("is_delivery", order.getIsDelivery());
        data.put("status", order.getStatus().getValue());
        data.put("address_id", order.getAddress().getId());
        Map<String, Object> details[] = new Map[order.getDetails().size()];
        for (int i = 0; i < details.length; i++) {
            details[i] = new HashMap();
            details[i].put("quantity", order.getDetails().get(i).getQuantity());
            details[i].put("product_id",
                order.getDetails().get(i).getProduct().getId());
        }
        data.put("details", details);
        data.put("notes", order.getNotes());

        new BackgroundSwingWorker(
            "/admin/orders",
            "POST",
            data,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Map<String, Object> orderMap = (Map<String, Object>) apiResponse.getData();

                GlobalCacheState.getOrders().add(0, transcriptOrder(orderMap));
                GlobalCacheState.syncOrders();

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
        },
            false).execute();
    }

    @Override
    public void update(Order order,
        ApiClient.onResponse onResponse) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(Order order,
        String field,
        ApiClient.onResponse onResponse) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete(int id,
        ApiClient.onResponse onResponse) {
        new BackgroundSwingWorker("/admin/orders/" + id, "DELETE", null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Iterator<Order> iterator = GlobalCacheState.getOrders().iterator();
                while (iterator.hasNext()) {
                    Order order = iterator.next();
                    if (order.getId() == id) {
                        iterator.remove();
                        break;
                    }
                }

                GlobalCacheState.syncOrders();

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

    public void getDataToSave(ApiClient.onResponse onResponse) {
        if (!GlobalCacheState.getCustomers().isEmpty() && !GlobalCacheState.getProducts().isEmpty()) {
            onResponse.onSuccess(new ApiClient.ApiResponse(
                ApiClient.ResponseType.SUCCESS,
                null,
                new Object[]{GlobalCacheState.getCustomers(), GlobalCacheState.getProducts()}
            ));
            return;
        }

        String endpoints[] = new String[]{"/admin/users", "/admin/products"};
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
                List<Map<String, Object>> customersMap = (List<Map<String, Object>>) response[0];
                List<Map<String, Object>> productsMap = (List<Map<String, Object>>) response[1];
                List<Customer> customers = GlobalCacheState.getCustomers();
                for (Map<String, Object> customerMap : customersMap) {
                    Customer customer = new Customer(
                        ((Number) customerMap.get("id")).intValue(),
                        (String) customerMap.get("name"),
                        (String) customerMap.get("email")
                    );
                    customer.setCreatedAtFromTimeStamp((String) customerMap.get(
                        "created_at"));
                    customer.setUpdatedAtFromTimeStamp((String) customerMap.get(
                        "updated_at"));
                    List<Map<String, Object>> adressesesMap = (List<Map<String, Object>>) customerMap.get(
                        "addresses"
                    );

                    List<Address> adresseses = customer.getAddresses();
                    for (Map<String, Object> addressMap : adressesesMap) {
                        adresseses.add(new Address(
                            ((Number) addressMap.get("id")).intValue(),
                            customer,
                            (String) addressMap.get("first_name"),
                            (String) addressMap.get("last_name"),
                            (String) addressMap.get("dni"),
                            (String) addressMap.get("phone"),
                            (String) addressMap.get("department"),
                            (String) addressMap.get("province"),
                            (String) addressMap.get("district"),
                            (String) addressMap.get("street_address"),
                            (String) addressMap.get("zip_code"),
                            (String) addressMap.get("reference")
                        ));
                    }
                    customers.add(customer);
                }

                List<Product> products = GlobalCacheState.getProducts();
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

                Object responses[] = {customers, products};
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

    public Cola<Order> getOrdersToDelivery() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Sale> getSales() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Order> findByDate(Date start_date,
        Date end_date) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Order> findBetweenDates(Date fecha) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Order> findOrdersByCustomer(Customer customer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
