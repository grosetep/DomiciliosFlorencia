package com.delivery.estrategiamovilmx.domiciliosflorencia.items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubscriptionItem implements Serializable {
   @SerializedName("valid")
   @Expose
   private String valid;
   @SerializedName("days_active")
   @Expose
   private String daysActive;
   @SerializedName("date_start")
   @Expose
   private String dateStart;
   @SerializedName("date_end")
   @Expose
   private String dateEnd;
   @SerializedName("id_suscription")
   @Expose
   private String idSuscription;
   @SerializedName("status_suscription")
   @Expose
   private String statusSuscription;
   @SerializedName("current_price")
   @Expose
   private String currentPrice;
   @SerializedName("new_price")
   @Expose
   private String newPrice;
   @SerializedName("date_new_price")
   @Expose
   private String dateNewPrice;
   @SerializedName("json_data")
   @Expose
   private String jsonData;
   @SerializedName("json_data_client")
   @Expose
   private String jsonDataClient;
   @SerializedName("status")
   @Expose
   private String status;
   @SerializedName("message")
   @Expose
   private String message;
   private final static long serialVersionUID = 6108560056779146155L;

   public String getValid() {
      return valid;
   }

   public void setValid(String valid) {
      this.valid = valid;
   }

   public String getDaysActive() {
      return daysActive;
   }

   public void setDaysActive(String daysActive) {
      this.daysActive = daysActive;
   }

   public String getDateStart() {
      return dateStart;
   }

   public void setDateStart(String dateStart) {
      this.dateStart = dateStart;
   }

   public String getDateEnd() {
      return dateEnd;
   }

   public void setDateEnd(String dateEnd) {
      this.dateEnd = dateEnd;
   }

   public String getIdSuscription() {
      return idSuscription;
   }

   public void setIdSuscription(String idSuscription) {
      this.idSuscription = idSuscription;
   }

   public String getStatusSuscription() {
      return statusSuscription;
   }

   public void setStatusSuscription(String statusSuscription) {
      this.statusSuscription = statusSuscription;
   }

   public String getCurrentPrice() {
      return currentPrice;
   }

   public void setCurrentPrice(String currentPrice) {
      this.currentPrice = currentPrice;
   }

   public String getNewPrice() {
      return newPrice;
   }

   public void setNewPrice(String newPrice) {
      this.newPrice = newPrice;
   }

   public String getDateNewPrice() {
      return dateNewPrice;
   }

   public void setDateNewPrice(String dateNewPrice) {
      this.dateNewPrice = dateNewPrice;
   }

   public String getJsonData() {
      return jsonData;
   }

   public void setJsonData(String jsonData) {
      this.jsonData = jsonData;
   }

   public String getJsonDataClient() {
      return jsonDataClient;
   }

   public void setJsonDataClient(String jsonDataClient) {
      this.jsonDataClient = jsonDataClient;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   @Override
   public String toString() {
      return "SubscriptionItem{" +
              "valid='" + valid + '\'' +
              ", daysActive='" + daysActive + '\'' +
              ", dateStart='" + dateStart + '\'' +
              ", dateEnd='" + dateEnd + '\'' +
              ", idSuscription='" + idSuscription + '\'' +
              ", statusSuscription='" + statusSuscription + '\'' +
              ", currentPrice='" + currentPrice + '\'' +
              ", newPrice='" + newPrice + '\'' +
              ", dateNewPrice='" + dateNewPrice + '\'' +
              ", jsonData='" + jsonData + '\'' +
              ", jsonDataClient='" + jsonDataClient + '\'' +
              ", status='" + status + '\'' +
              ", message='" + message + '\'' +
              '}';
   }
}