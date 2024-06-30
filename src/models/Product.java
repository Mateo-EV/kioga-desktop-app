/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author intel
 */
public class Product extends TimeStamps {

    private int id;
    private String name;
    private String slug;
    private String image;
    private String description;
    private double price;
    private double discount;
    private double price_discounted;
    private int stock;
    private Category category;
    private Brand brand;
    private boolean is_active;

    public Product() {
    }

    public Product(int id, String name, String slug, String description,
        String image, boolean isActive, double price, double discount, int stock,
        Category category, Brand brand) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.brand = brand;
        this.discount = discount;
    }

    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(
        new Locale("es", "PE"));

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public boolean isIsActive() {
        return is_active;
    }

    public void setIsActive(boolean is_active) {
        this.is_active = is_active;
    }

    public double getPriceDiscounted() {
        return price_discounted;
    }

    public void setPriceDiscounted(double price_discounted) {
        this.price_discounted = price_discounted;
    }

    public String getPriceDiscountedFormatted() {
        return currencyFormat.format(price_discounted);
    }
}
