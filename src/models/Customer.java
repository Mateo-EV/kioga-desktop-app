package models;

import java.util.ArrayList;
import java.util.List;

public class Customer extends TimeStamps implements Identifiable {

    private int id;
    private String name;
    private String email;
    private String password;

    private List<Address> addresses;

    public Customer() {
    }

    public Customer(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.addresses = new ArrayList();
    }

    public Customer(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.addresses = new ArrayList();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Address> getAddresses() {
        return addresses;
    }
}
