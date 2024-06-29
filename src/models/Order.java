package models;

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

public class Order extends TimeStamps {
    private int id;
    private String code;
    private double amount;
    private Customer user;
    private OrderStatus status;
    private Queue<OrderProduct> orderProducts = new LinkedList<>();
    private double shipping_amount;
    private boolean is_delivery;
    private Address address;
    private String notes;

    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));
    
    public Order() {}

    public Order(int id, String code, double amount, Customer user, OrderStatus status, double shipping_amount, boolean is_delivery, Address address, String notes) {
        this.id = id;
        this.code = code;
        this.amount = amount;
        this.user = user;
        this.status = status;
        this.shipping_amount = shipping_amount;
        this.is_delivery = is_delivery;
        this.address = address;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }
    
    public String getAmountFormatted() {
        return currencyFormat.format(amount);
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Customer getUser() {
        return user;
    }

    public void setUser(Customer user) {
        this.user = user;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Queue<OrderProduct> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(Queue<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public double getShippingAmount() {
        return shipping_amount;
    }
    
    public String getShippingAmountFormatted() {
        return currencyFormat.format(shipping_amount);
    }

    public void setShippingAmount(double shipping_amount) {
        this.shipping_amount = shipping_amount;
    }

    public boolean getIsDelivery() {
        return is_delivery;
    }

    public void setIsDelivery(boolean is_delivery) {
        this.is_delivery = is_delivery;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
