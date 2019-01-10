package com.delivery.estrategiamovilmx.domiciliosflorencia.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by administrator on 20/07/2017.
 */
public class ProductModel implements Serializable {
    @SerializedName("idProduct")
    @Expose
    private String idProduct;
    @SerializedName("product")
    @Expose
    private String product;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("regularPrice")
    @Expose
    private String regularPrice;
    @SerializedName("offerPrice")
    @Expose
    private String offerPrice;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("stock")
    @Expose
    private String stock;
    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("image")
    @Expose
    private String image;

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(String regularPrice) {
        this.regularPrice = regularPrice;
    }

    public String getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(String offerPrice) {
        this.offerPrice = offerPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
