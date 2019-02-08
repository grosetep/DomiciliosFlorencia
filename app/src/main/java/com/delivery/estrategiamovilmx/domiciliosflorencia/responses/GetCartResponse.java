package com.delivery.estrategiamovilmx.domiciliosflorencia.responses;


import com.delivery.estrategiamovilmx.domiciliosflorencia.items.CartProductItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by administrator on 08/08/2017.
 */
public class GetCartResponse implements Serializable{
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("result")
    @Expose
    private List<CartProductItem> result = null;
    @SerializedName("message")
    @Expose
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CartProductItem> getResult() {
        return result;
    }

    public void setResult(List<CartProductItem> result) {
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
        return "status:"+status+" list precio oferta:"+result.get(0).getRegularPrice() + " message:"+ message;
    }
}
