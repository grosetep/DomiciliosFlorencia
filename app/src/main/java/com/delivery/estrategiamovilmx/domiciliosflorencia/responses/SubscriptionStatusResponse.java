package com.delivery.estrategiamovilmx.domiciliosflorencia.responses;


import com.delivery.estrategiamovilmx.domiciliosflorencia.items.SubscriptionItem;

import java.io.Serializable;

public class SubscriptionStatusResponse implements Serializable {
   private String status;
   private SubscriptionItem result = null;
   private String message;

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public SubscriptionItem getResult() {
      return result;
   }

   public void setResult(SubscriptionItem result) {
      this.result = result;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   @Override
   public String toString() {
      return "SubscriptionStatusResponse{" +
              "status='" + status + '\'' +
              ", result=" + result +
              ", message='" + message + '\'' +
              '}';
   }
}