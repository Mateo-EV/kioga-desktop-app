/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author intel
 */
public class Sale extends Order {

    public Sale(
        int id,
        String code,
        double amount,
        Customer user,
        double shipping_amount,
        boolean is_delivery,
        Address address,
        String notes
    ) {
        super(
              id,
              code,
              amount,
              user,
              OrderStatus.ENTREGADO,
              shipping_amount,
              is_delivery,
              address,
              notes
        );
    }
}
