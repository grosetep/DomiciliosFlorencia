package com.delivery.estrategiamovilmx.domiciliosflorencia.model;


import com.delivery.estrategiamovilmx.domiciliosflorencia.items.CartProductItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by administrator on 10/08/2017.
 */
public class ShoppingCart implements Serializable {
    private String id_cart;
    private List<CartProductItem> products;
    private Float total;

    public String getId_cart() {
        return id_cart;
    }

    public void setId_cart(String id_cart) {
        this.id_cart = id_cart;
    }

    public List<CartProductItem> getProducts() {
        return products;
    }

    public void setProducts(List<CartProductItem> products) {
        this.products = products;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }
}
