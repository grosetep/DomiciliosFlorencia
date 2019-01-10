package com.delivery.estrategiamovilmx.domiciliosflorencia.items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by administrator on 15/08/2017.
 */
public class OrderItem implements Serializable {
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
    @SerializedName("deliverman_commision")
    @Expose
    private String deliverman_commision;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("vehicle")
    @Expose
    private String vehicle;
    @SerializedName("totalDestinations")
    @Expose
    private String totalDestinations;
    @SerializedName("timeFromOrigin")
    @Expose
    private String timeFromOrigin;
    @SerializedName("timeToOrigin")
    @Expose
    private String timeToOrigin;
    @SerializedName("destinations")
    @Expose
    private String destinations;
    @SerializedName("pathImageSignature")
    @Expose
    private String pathImageSignature;
    @SerializedName("imageNameSignature")
    @Expose
    private String imageNameSignature;
    @SerializedName("originalDestinations")
    @Expose
    private int originalDestinations;
    @SerializedName("isCircularTour")
    @Expose
    private int isCircularTour;

    private String signatureImageLocalPath;

    public String getSignatureImageLocalPath() {
        return signatureImageLocalPath;
    }

    public void setSignatureImageLocalPath(String signatureImageLocalPath) {
        this.signatureImageLocalPath = signatureImageLocalPath;
    }

    public String getPathImageSignature() {
        return pathImageSignature;
    }

    public void setPathImageSignature(String pathImageSignature) {
        this.pathImageSignature = pathImageSignature;
    }

    public String getImageNameSignature() {
        return imageNameSignature;
    }

    public void setImageNameSignature(String imageNameSignature) {
        this.imageNameSignature = imageNameSignature;
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

    public String getDeliverman_commision() {
        return deliverman_commision;
    }

    public void setDeliverman_commision(String deliverman_commision) {
        this.deliverman_commision = deliverman_commision;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getTotalDestinations() {
        return totalDestinations;
    }

    public void setTotalDestinations(String totalDestinations) {
        this.totalDestinations = totalDestinations;
    }

    public String getTimeFromOrigin() {
        return timeFromOrigin;
    }

    public void setTimeFromOrigin(String timeFromOrigin) {
        this.timeFromOrigin = timeFromOrigin;
    }

    public String getTimeToOrigin() {
        return timeToOrigin;
    }

    public void setTimeToOrigin(String timeToOrigin) {
        this.timeToOrigin = timeToOrigin;
    }

    public String getDestinations() {
        return destinations;
    }

    public void setDestinations(String destinations) {
        this.destinations = destinations;
    }

    public boolean compareTo(OrderItem element) {
        return this.getIdOrder().compareTo(element.getIdOrder()) == 0;
    }

    public int getOriginalDestinations() {
        return originalDestinations;
    }

    public void setOriginalDestinations(int originalDestinations) {
        this.originalDestinations = originalDestinations;
    }

    public int getIsCircularTour() {
        return isCircularTour;
    }

    public void setIsCircularTour(int isCircularTour) {
        this.isCircularTour = isCircularTour;
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
                ", deliverman_commision='" + deliverman_commision + '\'' +
                ", content='" + content + '\'' +
                ", vehicle='" + vehicle + '\'' +
                ", totalDestinations='" + totalDestinations + '\'' +
                ", timeFromOrigin='" + timeFromOrigin + '\'' +
                ", timeToOrigin='" + timeToOrigin + '\'' +
                ", destinations='" + destinations + '\'' +
                ", pathImageSignature='" + pathImageSignature + '\'' +
                ", imageNameSignature='" + imageNameSignature + '\'' +
                ", originalDestinations=" + originalDestinations +
                ", isCircularTour=" + isCircularTour +
                ", signatureImageLocalPath='" + signatureImageLocalPath + '\'' +
                '}';
    }
}
