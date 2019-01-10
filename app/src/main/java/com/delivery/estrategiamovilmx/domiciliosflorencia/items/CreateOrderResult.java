package com.delivery.estrategiamovilmx.domiciliosflorencia.items;

/**
 * Created by administrator on 11/08/2017.
 */
public class CreateOrderResult {
    private String newAddress;
    private String newContact;
    private String newOrder;
    private String cartCleaned;

    private String status;
    private String message;

    public String getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(String newAddress) {
        this.newAddress = newAddress;
    }

    public String getNewContact() {
        return newContact;
    }

    public void setNewContact(String newContact) {
        this.newContact = newContact;
    }

    public String getNewOrder() {
        return newOrder;
    }

    public void setNewOrder(String newOrder) {
        this.newOrder = newOrder;
    }

    public String getCartCleaned() {
        return cartCleaned;
    }

    public void setCartCleaned(String cartCleaned) {
        this.cartCleaned = cartCleaned;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "new_address:"+newAddress+", new_contact:"+newContact+", new_order:"+newOrder+", cart_cleaned:"+cartCleaned+" ,status: "+status+", message:"+message;
    }
}
