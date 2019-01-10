package com.delivery.estrategiamovilmx.domiciliosflorencia.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by administrator on 03/08/2017.
 */
public class Contact implements Serializable{
    @SerializedName("idContact")
    private String idContact;
    @SerializedName("name")
    private String name;
    @SerializedName("phone")
    private String phone;
    @SerializedName("isNew")
    private boolean isNew;
    @SerializedName("isSelected")
    private boolean isSelected;

    public String getIdContact() {
        return idContact;
    }

    public void setIdContact(String idContact) {
        this.idContact = idContact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "idContact:"+idContact+"name:"+name+"phone:"+phone;
    }
}
