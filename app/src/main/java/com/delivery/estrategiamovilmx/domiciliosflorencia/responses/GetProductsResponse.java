package com.delivery.estrategiamovilmx.domiciliosflorencia.responses;



import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ProductModel;

import java.util.List;

/**
 * Created by administrator on 21/07/2017.
 */
public class GetProductsResponse {
    private String status;
    private List<ProductModel> products = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ProductModel> getProducts() {
        return products;
    }

    public void setProducts(List<ProductModel> products) {
        this.products = products;
    }
}
