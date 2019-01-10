package com.delivery.estrategiamovilmx.domiciliosflorencia.responses;

import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ShippingAddress;

import java.util.List;

/**
 * Created by administrator on 26/07/2017.
 */
public class GetShippingAddressResponse {
    private String status;
    private List<ShippingAddress> result = null;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ShippingAddress> getResult() {
        return result;
    }

    public void setResult(List<ShippingAddress> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
