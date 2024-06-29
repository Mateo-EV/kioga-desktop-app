/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author intel
 */
public class Address {

    private int id;
    private Customer user;
    private String first_name;
    private String last_name;
    private String dni;
    private String phone;
    private String department;
    private String province;
    private String district;
    private String street_address;
    private String zip_code;
    private String reference;

    public Address() {
    }

    public Address(
            int id,
            Customer user,
            String first_name,
            String last_name,
            String dni,
            String phone,
            String department,
            String province,
            String district,
            String street_address,
            String zip_code,
            String reference
    ) {
        this.id = id;
        this.user = user;
        this.first_name = first_name;
        this.last_name = last_name;
        this.dni = dni;
        this.phone = phone;
        this.department = department;
        this.province = province;
        this.district = district;
        this.street_address = street_address;
        this.zip_code = zip_code;
        this.reference = reference;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getUser() {
        return user;
    }

    public void setUser(Customer user) {
        this.user = user;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet_address() {
        return street_address;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
