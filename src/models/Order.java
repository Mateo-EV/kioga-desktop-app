package models;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Order extends TimeStamps implements Identifiable {

    private int id;
    private String code;
    private double amount;
    private Customer customer;
    private OrderStatus status;
    private final List<OrderProduct> details = new ArrayList<>();
    private double shipping_amount;
    private boolean is_delivery;
    private Address address;
    private String notes;

    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(
        new Locale("es", "PE"));

    public Order() {
    }

    public Order(int id, String code, double amount, Customer customer,
        OrderStatus status, double shipping_amount, boolean is_delivery,
        Address address, String notes) {
        this.id = id;
        this.code = code;
        this.amount = amount;
        this.customer = customer;
        this.status = status;
        this.shipping_amount = shipping_amount;
        this.is_delivery = is_delivery;
        this.address = address;
        this.notes = notes;
    }

    @Override
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderProduct> getDetails() {
        return details;
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
