/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import java.util.ArrayList;
import java.util.List;
import models.Customer;
import utils.ApiClient;

/**
 *
 * @author intel
 */
public class CustomerController implements ModelController<Customer>{
    static List<Customer> customers = new ArrayList();
    
    @Override
    public void findById(int id, ApiClient.onResponse onResponse) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void findAll(ApiClient.onResponse onResponse) {
        
    }

    @Override
    public void save(Customer customer, ApiClient.onResponse onResponse) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(Customer customer, ApiClient.onResponse onResponse) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(Customer customer, String field, ApiClient.onResponse onResponse) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete(int id, ApiClient.onResponse onResponse) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    public Customer findByEmail(String email) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
