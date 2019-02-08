package com.delivery.estrategiamovilmx.domiciliosflorencia.responses;


import com.delivery.estrategiamovilmx.domiciliosflorencia.items.CategoryItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by administrator on 09/08/2017.
 */
public class CategoryResponse implements Serializable {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("result")
    @Expose
    private List<CategoryItem> result = null;
    @SerializedName("message")
    @Expose
    private String message;
    private final static long serialVersionUID = 7166477254607570658L;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CategoryItem> getResult() {
        return result;
    }

    public void setResult(List<CategoryItem> result) {
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
        return new String().concat("status:").concat(status).concat("categorias:").concat(""+result.size()).toString();
    }
}



