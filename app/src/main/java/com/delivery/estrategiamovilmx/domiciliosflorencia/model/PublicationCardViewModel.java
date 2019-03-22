package com.delivery.estrategiamovilmx.domiciliosflorencia.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by administrator on 10/07/2017.
 */
public class PublicationCardViewModel implements Serializable {
    @SerializedName("idProduct")
    @Expose
    private String idProduct;
    @SerializedName("idMerchant")
    @Expose
    private String idMerchant;
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
    @SerializedName("added")
    @Expose
    private boolean added;

    public String getIdMerchant() {
        return idMerchant;
    }

    public void setIdMerchant(String idMerchant) {
        this.idMerchant = idMerchant;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

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

    @Override
    public String toString() {
        return "PublicationCardViewModel{" +
                "idProduct='" + idProduct + '\'' +
                ", idMerchant='" + idMerchant + '\'' +
                ", product='" + product + '\'' +
                ", description='" + description + '\'' +
                ", regularPrice='" + regularPrice + '\'' +
                ", offerPrice='" + offerPrice + '\'' +
                ", discount='" + discount + '\'' +
                ", stock='" + stock + '\'' +
                ", path='" + path + '\'' +
                ", image='" + image + '\'' +
                ", added=" + added +
                '}';
    }

}
