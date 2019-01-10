package com.delivery.estrategiamovilmx.domiciliosflorencia.responses;

import com.delivery.estrategiamovilmx.domiciliosflorencia.items.OrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by administrator on 15/08/2017.
 */
public class GetOrdersResponse implements Serializable {
    private String status;
    private List<OrderItem> result = null;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItem> getResult() {
        return result;
    }

    public void setResult(List<OrderItem> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
