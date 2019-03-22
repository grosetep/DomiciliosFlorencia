package com.delivery.estrategiamovilmx.domiciliosflorencia.responses;

import com.delivery.estrategiamovilmx.domiciliosflorencia.items.OrderPurchaseItem;

import java.io.Serializable;
import java.util.List;

public class GetOrdersPurchaseResponse implements Serializable {
    private String status;
    private List<OrderPurchaseItem> result = null;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderPurchaseItem> getResult() {
        return result;
    }

    public void setResult(List<OrderPurchaseItem> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "GetOrdersPurchaseResponse{" +
                "status='" + status + '\'' +
                ", result=" + result +
                ", message='" + message + '\'' +
                '}';
    }
}
