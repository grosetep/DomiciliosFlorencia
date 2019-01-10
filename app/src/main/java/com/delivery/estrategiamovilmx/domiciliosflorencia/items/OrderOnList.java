package com.delivery.estrategiamovilmx.domiciliosflorencia.items;

import java.io.Serializable;

public class OrderOnList implements Serializable {
    private int position;
    private String statusToUpdate;
    private String comment;

    public OrderOnList(int position, String statusToUpdate, String comment) {
        this.position = position;
        this.statusToUpdate = statusToUpdate;
        this.comment = comment;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getStatusToUpdate() {
        return statusToUpdate;
    }

    public void setStatusToUpdate(String statusToUpdate) {
        this.statusToUpdate = statusToUpdate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}