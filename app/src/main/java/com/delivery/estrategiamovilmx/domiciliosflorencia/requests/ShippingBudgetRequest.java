package com.delivery.estrategiamovilmx.domiciliosflorencia.requests;

import com.delivery.estrategiamovilmx.domiciliosflorencia.model.OrderShipping;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by administrator on 10/08/2017.
 */
public class ShippingBudgetRequest implements Serializable{
    @SerializedName("id_order")
    private String id_order;
    @SerializedName("order_shipping")
    private OrderShipping order_shipping;

    public String getId_order() {
        return id_order;
    }

    public void setId_order(String id_order) {
        this.id_order = id_order;
    }

    public OrderShipping getOrder_shipping() {
        return order_shipping;
    }

    public void setOrder_shipping(OrderShipping order_shipping) {
        this.order_shipping = order_shipping;
    }
}
