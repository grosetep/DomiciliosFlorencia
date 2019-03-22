package com.delivery.estrategiamovilmx.domiciliosflorencia.items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderPurchaseItem implements Serializable {
    @SerializedName("idOrder")
    @Expose
    private String idOrder;
    @SerializedName("idUser")
    @Expose
    private String idUser;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("total")
    @Expose
    private String total;
    @SerializedName("review_day")
    @Expose
    private String review_day;
    @SerializedName("review_num_day")
    @Expose
    private String review_num_day;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("moneyBack")
    @Expose
    private String moneyBack;

    public String getMoneyBack() {
        return moneyBack;
    }

    public void setMoneyBack(String moneyBack) {
        this.moneyBack = moneyBack;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getReview_day() {
        return review_day;
    }

    public void setReview_day(String review_day) {
        this.review_day = review_day;
    }

    public String getReview_num_day() {
        return review_num_day;
    }

    public void setReview_num_day(String review_num_day) {
        this.review_num_day = review_num_day;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "idOrder='" + idOrder + '\'' +
                ", idUser='" + idUser + '\'' +
                ", status='" + status + '\'' +
                ", total='" + total + '\'' +
                ", review_day='" + review_day + '\'' +
                ", review_num_day='" + review_num_day + '\'' +
                ", comment='" + comment + '\'' +
                ", moneyBack='" + moneyBack + '\'' +
                '}';
    }

    public boolean compareTo(OrderItem element) {
        return this.getIdOrder().compareTo(element.getIdOrder()) == 0;
    }
}
