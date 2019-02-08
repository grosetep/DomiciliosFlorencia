package com.delivery.estrategiamovilmx.domiciliosflorencia.responses;



import com.delivery.estrategiamovilmx.domiciliosflorencia.model.Contact;

import java.util.List;

/**
 * Created by administrator on 03/08/2017.
 */
public class GetContactsResponse {
    private String status;
    private List<Contact> result = null;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Contact> getResult() {
        return result;
    }

    public void setResult(List<Contact> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
