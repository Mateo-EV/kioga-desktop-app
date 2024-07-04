package controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Address;
import models.Customer;
import models.Order;
import models.OrderProduct;
import models.OrderStatus;
import models.Product;
import models.Sale;
import utils.ApiClient;
import utils.BackgroundSwingWorker;
import utils.GlobalCacheState;
import utils.structure.ArbolBinario;
import utils.structure.Cola;

public class OrderController implements ModelController<Order> {

    private static final OrderController instance = new OrderController();

    static public OrderController getInstance() {
        return instance;
    }

    static public Order transcriptOrder(Map<String, Object> orderMap) {
        Order order = new Order();
        order.setId(((Number) orderMap.get("id")).intValue());
        order.setCode((String) orderMap.get("code"));
        order.setAmount((double) orderMap.get("amount"));
        order.setIsDelivery((boolean) orderMap.get("is_delivery"));
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

    static public OrderProduct transcriptDetail(Map<String, Object> detailsMap) {
        OrderProduct detail = new OrderProduct();
        detail.setQuantity(((Number) detailsMap.get("quantity")).intValue());
        detail.setUnitAmount((double) detailsMap.get("unit_amount"));

        return detail;
    }

    static public Address transcriptAddress(Map<String, Object> addressMap,
        Customer customer) {
        return new Address(
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
        );
    }

    @Override
    public void findById(int id,
        ApiClient.onResponse onResponse) {
        Order order_cached = GlobalCacheState.getOrders().find(id);
        if (order_cached != null && order_cached.getAddress() != null
            && order_cached.getDetails() != null
            && order_cached.getCustomer() != null) {
            onResponse.onSuccess(new ApiClient.ApiResponse(
                ApiClient.ResponseType.SUCCESS,
                null,
                order_cached
            ));
            return;
        }

        new BackgroundSwingWorker(
            "/admin/orders/" + id,
            "GET",
            null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Map<String, Object> ordersMap = (Map<String, Object>) apiResponse.getData();

                Order order = transcriptOrder(ordersMap);
                List<Map<String, Object>> detailsMap = (List<Map<String, Object>>) ordersMap.get(
                    "details");

                Customer customer = CustomerController.transcriptCustomer(
                    (Map<String, Object>) ordersMap.get("user"));

                Address address = transcriptAddress(
                    (Map<String, Object>) ordersMap.get("address"), customer);

                order.setAddress(address);
                order.setCustomer(customer);
                for (Map<String, Object> detailMap : detailsMap) {
                    OrderProduct detail = transcriptDetail(detailMap);
                    Product product = ProductController.transcriptProduct(
                        (Map<String, Object>) detailMap.get("product"));
                    detail.setProduct(product);
                    order.getDetails().add(detail);
                }

                GlobalCacheState.getOrders().update(order);

                onResponse.onSuccess(new ApiClient.ApiResponse(
                    ApiClient.ResponseType.SUCCESS,
                    null,
                    order
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
                ArbolBinario<Order> orders = GlobalCacheState.getOrders();
                for (Map<String, Object> orderMap : ordersMap) {
                    orders.insert(transcriptOrder(orderMap));
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
        if (order.getAddress().getId() != 0) {
            data.put("address_id", order.getAddress().getId());
        } else {
            Map<String, Object> addressData = new HashMap<>();
            addressData.put("first_name", order.getAddress().getFirstName());
            addressData.put("last_name", order.getAddress().getLastName());
            addressData.put("dni", order.getAddress().getDni());
            addressData.put("phone", order.getAddress().getPhone());
            addressData.put("department", order.getAddress().getDepartment());
            addressData.put("province", order.getAddress().getProvince());
            addressData.put("district", order.getAddress().getDistrict());
            addressData.put("street_address",
                order.getAddress().getStreetAddress());
            addressData.put("zip_code", order.getAddress().getZipCode());
            addressData.put("reference", order.getAddress().getReference());

            data.put("address", addressData);
        }

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

                GlobalCacheState.getOrders().insert(transcriptOrder(orderMap));
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

        Map<String, Object> data = new HashMap<>();
        data.put("user_id", order.getCustomer().getId());
        data.put("is_delivery", order.getIsDelivery());
        data.put("status", order.getStatus().getValue());
        data.put("address_id", order.getAddress().getId());

        Map<String, Object> details[] = new Map[order.getDetails().size()];
        for (int i = 0; i < details.length; i++) {
            details[i] = new HashMap();
            System.out.println(order.getDetails().get(i).getQuantity());
            details[i].put("quantity", order.getDetails().get(i).getQuantity());
            details[i].put("product_id",
                order.getDetails().get(i).getProduct().getId());
        }
        data.put("details", details);
        data.put("notes", order.getNotes());

        new BackgroundSwingWorker(
            "/admin/orders/" + order.getId(),
            "PUT",
            data,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Map<String, Object> orderMap = (Map<String, Object>) apiResponse.getData();

                GlobalCacheState.getOrders().update(transcriptOrder(orderMap));
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

    public void updateStatus(Order order,
        ApiClient.onResponse onResponse) {
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("status", order.getStatus().getValue());

        new BackgroundSwingWorker("/admin/orders/status/" + order.getId(),
            "POST",
            statusMap, new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
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

    @Override
    public void delete(int id,
        ApiClient.onResponse onResponse) {
        new BackgroundSwingWorker("/admin/orders/" + id, "DELETE", null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                GlobalCacheState.getOrders().delete(id);
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

                ArbolBinario<Customer> customers = GlobalCacheState.getCustomers();
                for (Map<String, Object> customerMap : customersMap) {
                    Customer customer = CustomerController.transcriptCustomer(
                        customerMap);
                    List<Map<String, Object>> adressesesMap = (List<Map<String, Object>>) customerMap.get(
                        "addresses"
                    );
                    List<Address> adresseses = customer.getAddresses();
                    for (Map<String, Object> addressMap : adressesesMap) {
                        adresseses.add(transcriptAddress(addressMap, customer));
                    }
                    customers.insert(customer);
                }

                ArbolBinario<Product> products = GlobalCacheState.getProducts();
                for (Map<String, Object> productMap : productsMap) {
                    Product product = ProductController.transcriptProduct(
                        productMap);
                    products.insert(product);
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

        Cola<Order> orders = new Cola();
        GlobalCacheState.getOrders().forEach((order) -> {
            if (order.getStatus() == OrderStatus.PENDING) {
                orders.enqueue(order);
            }
        });

        return orders;
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
