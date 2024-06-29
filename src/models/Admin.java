/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author intel
 */
public class Admin {
    private String name;
    private String email;
    private String image;
    private final String base_image_url = "https://ui-avatars.com/api/?name=";
    
    public Admin(){};

    public Admin(String name, String email) {
        this.name = name;
        this.email = email;
        this.image = base_image_url.concat(name);
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.image = base_image_url.concat(name);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        System.out.println(image);
        return image;
    }
}
