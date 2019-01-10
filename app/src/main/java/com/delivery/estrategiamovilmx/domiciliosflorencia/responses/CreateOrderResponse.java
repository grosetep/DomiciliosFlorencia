package com.delivery.estrategiamovilmx.domiciliosflorencia.responses;


import com.delivery.estrategiamovilmx.domiciliosflorencia.items.CreateOrderResult;

/**
 * Created by administrator on 11/08/2017.
 */
public class CreateOrderResponse {
    private String status;
    private CreateOrderResult result;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CreateOrderResult getResult() {
        return result;
    }

    public void setResult(CreateOrderResult result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
