package com.delivery.estrategiamovilmx.domiciliosflorencia.items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by administrator on 09/08/2017.
 */
public class CartProductItem implements Serializable{
    @SerializedName("idCart")
    @Expose
    private String idCart;
    @SerializedName("idProduct")
    @Expose
    private String idProduct;
    @SerializedName("product")
    @Expose
    private String product;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("units")
    @Expose
    private String units;
    @SerializedName("regularPrice")
    @Expose
    private String regularPrice;
    @SerializedName("offerPrice")
    @Expose
    private String offerPrice;
    @SerializedName("stock")
    @Expose
    private String stock;
    @SerializedName("route")
    @Expose
    private String route;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("total")
    @Expose
    private String total;
    @SerializedName("variant")
    @Expose
    private String variant;
    @SerializedName("additionals")
    @Expose
    private String additionals;




    public String getIdCart() {
        return idCart;
    }

    public void setIdCart(String idCart) {
        this.idCart = idCart;
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

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
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

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }


    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getAdditionals() {
        return additionals;
    }

    public void setAdditionals(String additionals) {
        this.additionals = additionals;
    }

    @Override
    public String toString() {
        return "idCarrito:"+idCart+"producto:"+idProduct+":"+product + " descripción:" + description + " units: " + units +
                " regularPrice:"+regularPrice+ ",offerPrice: "+offerPrice+"stock:"+stock+"image:"+route+image;
    }
}