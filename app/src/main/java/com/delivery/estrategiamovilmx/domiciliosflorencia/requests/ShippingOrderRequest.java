package com.delivery.estrategiamovilmx.domiciliosflorencia.requests;

import com.delivery.estrategiamovilmx.domiciliosflorencia.items.BudgetResult;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.OrderShipping;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by administrator on 10/08/2017.
 */
public class ShippingOrderRequest implements Serializable{
    @SerializedName("id_order")
    private String id_order;
    @SerializedName("order_shipping")
    private OrderShipping order_shipping;
    @SerializedName("budget")
    private BudgetResult budget;
    @SerializedName("user")
    private UserItem user;
    @SerializedName("businessName")
    private String businessName;
    @SerializedName("token")
    private String token;

    public UserItem getUser() {
        return user;
    }

    public void setUser(UserItem user) {
        this.user = user;
    }

    public BudgetResult getBudget() {
        return budget;
    }

    public void setBudget(BudgetResult budget) {
        this.budget = budget;
    }

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

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
