package com.delivery.estrategiamovilmx.domiciliosflorencia.requests;

import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by administrator on 17/08/2017.
 */
public class UserOperationRequest implements Serializable {
    @SerializedName("user")
    private UserItem user;
    @SerializedName("operation")
    private String operation;
    @SerializedName("operationSecondary")
    private String operationSecondary;


    public UserItem getUser() {
        return user;
    }

    public void setUser(UserItem user) {
        this.user = user;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperationSecondary() {
        return operationSecondary;
    }

    public void setOperationSecondary(String operationSecondary) {
        this.operationSecondary = operationSecondary;
    }
}
