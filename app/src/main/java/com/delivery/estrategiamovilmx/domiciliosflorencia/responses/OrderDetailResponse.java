package com.delivery.estrategiamovilmx.domiciliosflorencia.responses;



import com.delivery.estrategiamovilmx.domiciliosflorencia.items.OrderDetail;

import java.io.Serializable;

/**
 * Created by administrator on 04/09/2017.
 */
public class OrderDetailResponse implements Serializable {
    private String status;
    private OrderDetail result = null;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrderDetail getResult() {
        return result;
    }

    public void setResult(OrderDetail result) {
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
        return "OrderDetailResponse{" +
                "status='" + status + '\'' +
                ", result=" + result +
                ", message='" + message + '\'' +
                '}';
    }
}
