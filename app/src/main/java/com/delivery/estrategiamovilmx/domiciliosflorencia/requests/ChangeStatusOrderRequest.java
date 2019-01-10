package com.delivery.estrategiamovilmx.domiciliosflorencia.requests;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by administrator on 24/08/2017.
 */
public class ChangeStatusOrderRequest implements Serializable {
    @SerializedName("idUser")
    private String idUser;
    @SerializedName("statusToUpdate")
    private String statusToUpdate;
    @SerializedName("idOrder")
    private String idOrder;
    @SerializedName("comment")
    private String comment;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getStatusToUpdate() {
        return statusToUpdate;
    }

    public void setStatusToUpdate(String statusToUpdate) {
        this.statusToUpdate = statusToUpdate;
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "ChangeStatusOrderRequest{" +
                "idUser='" + idUser + '\'' +
                ", statusToUpdate='" + statusToUpdate + '\'' +
                ", idOrder='" + idOrder + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
