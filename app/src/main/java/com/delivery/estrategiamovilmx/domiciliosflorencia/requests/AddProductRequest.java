package com.delivery.estrategiamovilmx.domiciliosflorencia.requests;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by administrator on 08/08/2017.
 *
 */
public class AddProductRequest implements Serializable {
    @SerializedName("id_user")
    private String id_user;
    @SerializedName("id_product")
    private String id_product;
    @SerializedName("units")
    private String units;
    @SerializedName("operation")
    private String operation;
    @SerializedName("price_product")
    private String price_product;
    @SerializedName("total")
    private String total;
    @SerializedName("id_variant")
    private String id_variant;
    @SerializedName("list_additionals")
    private String list_additionals;
    @SerializedName("comment")
    private String comment;


    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getId_product() {
        return id_product;
    }

    public void setId_product(String id_product) {
        this.id_product = id_product;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getPrice_product() {
        return price_product;
    }

    public void setPrice_product(String price_product) {
        this.price_product = price_product;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getId_variant() {
        return id_variant;
    }

    public void setId_variant(String id_variant) {
        this.id_variant = id_variant;
    }

    public String getList_additionals() {
        return list_additionals;
    }

    public void setList_additionals(String list_additionals) {
        this.list_additionals = list_additionals;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "AddProductRequest{" +
                "id_user='" + id_user + '\'' +
                ", id_product='" + id_product + '\'' +
                ", units='" + units + '\'' +
                ", operation='" + operation + '\'' +
                ", price_product='" + price_product + '\'' +
                ", total='" + total + '\'' +
                ", id_variant='" + id_variant + '\'' +
                ", list_additionals='" + list_additionals + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
