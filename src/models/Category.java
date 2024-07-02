/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author intel
 */
public class Category extends TimeStamps implements Identifiable {

    private int id;
    private String name;
    private String image;
    private File imageFile;
    private String slug;
    private final List<Subcategory> subcategories = new ArrayList<>();

    public Category() {
    }

    public Category(int id, String name, String slug, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.slug = slug;
    }

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public List<Subcategory> getSubcategories() {
        return subcategories;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

}
