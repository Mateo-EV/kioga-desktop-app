/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author intel
 */
public class OrderProduct {
    private Order order;
    private Product product;
    private int quantity;
    private double unit_amount;
    private double total_amount;
    
    public OrderProduct(){}

    public OrderProduct(Order order, Product product, int quantity, double unit_amount, double total_amount) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unit_amount = unit_amount;
        this.total_amount = total_amount;
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

    public double getUnit_amount() {
        return unit_amount;
    }

    public void setUnit_amount(double unit_amount) {
        this.unit_amount = unit_amount;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }
}
