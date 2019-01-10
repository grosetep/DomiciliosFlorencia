package com.delivery.estrategiamovilmx.domiciliosflorencia.responses;


import com.delivery.estrategiamovilmx.domiciliosflorencia.items.BudgetResult;

import java.io.Serializable;

/**
 * Created by administrator on 18/08/2017.
 */
public class BudgetResponse implements Serializable {
        private String status;
        private BudgetResult result;
        private String message;
        private final static long serialVersionUID = -8446478936205702907L;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BudgetResult getResult() {
        return result;
    }

    public void setResult(BudgetResult result) {
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
        return new StringBuffer().append("status:"+ status).append("result:"+ result).append("message:"+ message).toString();
    }

}
