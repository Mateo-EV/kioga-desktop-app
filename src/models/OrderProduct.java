package models;

import java.text.NumberFormat;
import java.util.Locale;

public class OrderProduct {

    private Order order;
    private Product product;
    private int quantity;
    private double unit_amount;

    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(
        new Locale("es", "PE"));

    public OrderProduct() {
    }

    public OrderProduct(Order order, Product product, int quantity,
        double unit_amount) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unit_amount = unit_amount;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitAmount() {
        return unit_amount;
    }

    public void setUnitAmount(double unit_amount) {
        this.unit_amount = unit_amount;
    }

    public String getUnitAmountFormatted() {
        return currencyFormat.format(unit_amount);
    }
}
