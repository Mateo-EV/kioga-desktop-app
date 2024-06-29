/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author intel
 */
public class Subcategory {
    private int id;
    private String name;
    private Category cateogory;

    public Subcategory() {}

    public Subcategory(int id, String name, Category cateogory) {
        this.id = id;
        this.name = name;
        this.cateogory = cateogory;
    }

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

    public Category getCateogory() {
        return cateogory;
    }

    public void setCateogory(Category cateogory) {
        this.cateogory = cateogory;
    }
}
