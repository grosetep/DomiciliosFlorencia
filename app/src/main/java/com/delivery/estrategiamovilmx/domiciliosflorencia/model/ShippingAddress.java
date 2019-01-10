package com.delivery.estrategiamovilmx.domiciliosflorencia.model;


import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by administrator on 26/07/2017.
 */
public class ShippingAddress implements Serializable {
    @SerializedName("id_location")
    private String id_location;
    @SerializedName("street")
    private String street;
    @SerializedName("postal_code")
    private String postal_code;
    @SerializedName("num_ext")
    private String num_ext;
    @SerializedName("num_int")
    private String num_int;
    @SerializedName("no_number")
    private String no_number;
    @SerializedName("between_streets")
    private String between_streets;
    @SerializedName("reference")
    private String reference;
    @SerializedName("municipality")
    private String municipality;
    @SerializedName("town")
    private String town;
    @SerializedName("country")
    private String country;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("googlePlace")
    private String googlePlace;
    @SerializedName("isNew")
    private String isNew;
    @SerializedName("isSelected")
    private boolean isSelected;
    @SerializedName("userTypeAddress")
    private String userTypeAddress;
    @SerializedName("placeId")
    private String placeId;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getUserTypeAddress() {
        return userTypeAddress;
    }

    public void setUserTypeAddress(String userTypeAddress) {
        this.userTypeAddress = userTypeAddress;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    public String getGooglePlace() {
        return googlePlace;
    }

    public void setGooglePlace(String googlePlace) {
        this.googlePlace = googlePlace;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getId_location() {
        return id_location;
    }

    public void setId_location(String id_location) {
        this.id_location = id_location;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getNum_ext() {
        return num_ext;
    }

    public void setNum_ext(String num_ext) {
        this.num_ext = num_ext;
    }

    public String getNum_int() {
        return num_int;
    }

    public void setNum_int(String num_int) {
        this.num_int = num_int;
    }

    public String getNo_number() {
        return no_number;
    }

    public void setNo_number(String no_number) {
        this.no_number = no_number;
    }

    public String getBetween_streets() {
        return between_streets;
    }

    public void setBetween_streets(String between_streets) {
        this.between_streets = between_streets;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
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

    @Override
    public String toString() {
        return "id location: "+id_location + " googlePlace: "+ googlePlace + " , Numero interior: " + num_int + " referencia: "+ reference;
    }
    public String getAddressForUser(){
        StringBuffer show_address  = new StringBuffer();
        show_address.append(getGooglePlace().length()> Constants.address_max_length?getGooglePlace().substring(0,Constants.address_max_length)+"...,":getGooglePlace());
        show_address.append(", ");
        show_address.append((getNum_int()!=null && !getNum_int().isEmpty())?getNum_int():"");
        show_address.append(", ").append(getReference());
        return show_address.toString();
    }
    public String getAddressShort(){
        StringBuffer show_address  = new StringBuffer();
        show_address.append(getGooglePlace().length()> Constants.address_max_length?getGooglePlace().substring(0,Constants.address_max_length)+"...,":getGooglePlace());
        return show_address.toString();
    }
    public String getAddressShortExtra(){
        StringBuffer show_address  = new StringBuffer();
        show_address.append((getNum_int()!=null && !getNum_int().isEmpty())?getNum_int()+", ":"");
        show_address.append(getReference());
        return show_address.toString();
    }

}
