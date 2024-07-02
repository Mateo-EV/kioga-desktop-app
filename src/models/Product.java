package models;

import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;

public class Product extends TimeStamps implements Identifiable {

    private int id;
    private String name;
    private String slug;
    private String image;
    private File imageFile;
    private String description;
    private double price;
    private double discount;
    private double price_discounted;
    private int stock;
    private Category category;
    private Subcategory subcategory;
    private Brand brand;
    private boolean is_active;

    public Product() {
    }

    public Product(int id, String name, String slug, String description,
        String image, boolean isActive, double price, double discount, int stock,
        Category category, Subcategory subcategory, Brand brand) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.subcategory = subcategory;
        this.brand = brand;
        this.discount = discount;
        this.is_active = isActive;
        this.imageFile = null;
    }

    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(
        new Locale("es", "PE"));

    @Override
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

    public boolean getIsActive() {
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

    public Subcategory getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(Subcategory subcategory) {
        this.subcategory = subcategory;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }
}
