package com.delivery.estrategiamovilmx.domiciliosflorencia.items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderDetailPurchase implements Serializable {
    @SerializedName("idOrder")
    @Expose
    private String idOrder;
    @SerializedName("statusOrder")
    @Expose
    private String statusOrder;
    @SerializedName("total")
    @Expose
    private String total;
    @SerializedName("googlePlace")
    @Expose
    private String googlePlace;
    @SerializedName("numInt")
    @Expose
    private String numInt;
    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("emailClient")
    @Expose
    private String emailClient;
    @SerializedName("nameClient")
    @Expose
    private String nameClient;
    @SerializedName("contactName")
    @Expose
    private String contactName;
    @SerializedName("contactPhone")
    @Expose
    private String contactPhone;
    @SerializedName("products")
    @Expose
    private String products;
    @SerializedName("additionals")
    @Expose
    private String additionals;
    @SerializedName("hourCreation")
    @Expose
    private String hourCreation;
    @SerializedName("dateCreation")
    @Expose
    private String dateCreation;
    @SerializedName("variants")
    @Expose
    private String variants;
    @SerializedName("payment")
    @Expose
    private String payment;

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getVariants() {
        return variants;
    }

    public void setVariants(String variants) {
        this.variants = variants;
    }

    public String getAdditionals() {
        return additionals;
    }

    public void setAdditionals(String additionals) {
        this.additionals = additionals;
    }

    public String getEmailClient() {
        return emailClient;
    }

    public void setEmailClient(String emailClient) {
        this.emailClient = emailClient;
    }

    public String getNameClient() {
        return nameClient;
    }

    public void setNameClient(String nameClient) {
        this.nameClient = nameClient;
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public String getStatusOrder() {
        return statusOrder;
    }

    public void setStatusOrder(String statusOrder) {
        this.statusOrder = statusOrder;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getGooglePlace() {
        return googlePlace;
    }

    public void setGooglePlace(String googlePlace) {
        this.googlePlace = googlePlace;
    }

    public String getNumInt() {
        return numInt;
    }

    public void setNumInt(String numInt) {
        this.numInt = numInt;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public String getHourCreation() {
        return hourCreation;
    }

    public void setHourCreation(String hourCreation) {
        this.hourCreation = hourCreation;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "idOrder='" + idOrder + '\'' +
                ", statusOrder='" + statusOrder + '\'' +
                ", total='" + total + '\'' +
                ", googlePlace='" + googlePlace + '\'' +
                ", numInt='" + numInt + '\'' +
                ", reference='" + reference + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", emailClient='" + emailClient + '\'' +
                ", nameClient='" + nameClient + '\'' +
                ", contactName='" + contactName + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", products='" + products + '\'' +
                ", additionals='" + additionals + '\'' +
                ", hourCreation='" + hourCreation + '\'' +
                ", dateCreation='" + dateCreation + '\'' +
                ", variants='" + variants + '\'' +
                ", payment='" + payment + '\'' +
                '}';
    }
}
