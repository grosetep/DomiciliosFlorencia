package com.delivery.estrategiamovilmx.domiciliosflorencia.model;

import java.io.Serializable;

/**
 * Created by administrator on 26/07/2017.
 */
public class PickupAddress implements Serializable, Comparable{
    private ShippingAddress googlePlace;
    private String latitude;
    private String longitude;
    private String postalCode;
    private String nameContact;
    private String phoneContact;
    private String dateShipping;
    private String startHour;
    private String endHour;
    private String comment;
    private int typeAddress;
    private int pickupPointNumber;
    private String placeId;
    private String distance;
    private boolean addedByCircularTour;

    public boolean isAddedByCircularTour() {
        return addedByCircularTour;
    }


    public void setAddedByCircularTour(boolean addedByCircularTour) {
        this.addedByCircularTour = addedByCircularTour;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public int getPickupPointNumber() {
        return pickupPointNumber;
    }

    public void setPickupPointNumber(int pickupPointNumber) {
        this.pickupPointNumber = pickupPointNumber;
    }

    public int getTypeAddress() {
        return typeAddress;
    }

    public void setTypeAddress(int typeAddress) {
        this.typeAddress = typeAddress;
    }

    public ShippingAddress getGooglePlace() {
        return googlePlace;
    }

    public void setGooglePlace(ShippingAddress googlePlace) {
        this.googlePlace = googlePlace;
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

    public String getNameContact() {
        return nameContact;
    }

    public void setNameContact(String nameContact) {
        this.nameContact = nameContact;
    }

    public String getPhoneContact() {
        return phoneContact;
    }

    public void setPhoneContact(String phoneContact) {
        this.phoneContact = phoneContact;
    }

    public String getDateShipping() {
        return dateShipping;
    }

    public void setDateShipping(String dateShipping) {
        this.dateShipping = dateShipping;
    }

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public String getEndHour() {
        return endHour;
    }

    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    @Override
    public int compareTo(Object o) {
        //orden ascendente
        if (((PickupAddress) o).getPickupPointNumber() > getPickupPointNumber())
            return -1;
        else if (((PickupAddress) o).getPickupPointNumber() < getPickupPointNumber())
            return 1;
        else
            return 0;
    }

    @Override
    public String toString() {
        return "PickupAddress{" +
                "googlePlace='" + googlePlace + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", nameContact='" + nameContact + '\'' +
                ", phoneContact='" + phoneContact + '\'' +
                ", dateShipping='" + dateShipping + '\'' +
                ", startHour='" + startHour + '\'' +
                ", endHour='" + endHour + '\'' +
                ", comment='" + comment + '\'' +
                ", typeAddress=" + typeAddress +
                ", pickupPointNumber=" + pickupPointNumber +
                ", placeId='" + placeId + '\'' +
                ", distance='" + distance + '\'' +
                ", addedByCircularTour=" + addedByCircularTour +
                '}';
    }

    public static PickupAddress getCopy(PickupAddress origin){
        PickupAddress new_object = new PickupAddress();
        if (origin!=null){
            new_object.setNameContact(origin.getNameContact());
            new_object.setPhoneContact(origin.getPhoneContact());
            new_object.setPickupPointNumber(origin.getPickupPointNumber());
            new_object.setAddedByCircularTour(origin.isAddedByCircularTour());
            new_object.setComment(origin.getComment());
            new_object.setDateShipping(origin.getDateShipping());
            new_object.setDistance(origin.getDistance());
            new_object.setEndHour(origin.getEndHour());
            new_object.setGooglePlace(origin.getGooglePlace());
            new_object.setLatitude(origin.getLatitude());
            new_object.setLongitude(origin.getLongitude());
            new_object.setPlaceId(origin.getPlaceId());
            new_object.setPostalCode(origin.getPostalCode());
            new_object.setStartHour(origin.getStartHour());
            new_object.setTypeAddress(origin.getTypeAddress());

        }
        return new_object;
    }
}
