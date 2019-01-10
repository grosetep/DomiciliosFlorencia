package com.delivery.estrategiamovilmx.domiciliosflorencia.model;

public class DestinyView {
    ////pickup_point_number,'@@@',hora_desde,'@@@',hora_hasta,'@@@',google_place,'@@@',distancia,'@@@',contacto,'@@@',telefono_contacto,'@@@',comentario,'@@@',tipo_direccion,'@@@',latitud,'@@@',longitud
    private String pickupPointNumber;
    private String time_from;
    private String time_to;
    private String googlePlace;
    private String distance;
    private String conctac;
    private String contactPhone;
    private String comment;
    private String typeAddress;
    private String latitude;
    private String longitude;

    public DestinyView(String pickupPointNumber, String time_from, String time_to, String googlePlace, String distance, String conctac, String contactPhone, String comment, String typeAddress, String latitude, String longitude) {
        this.pickupPointNumber = pickupPointNumber;
        this.time_from = time_from;
        this.time_to = time_to;
        this.googlePlace = googlePlace;
        this.distance = distance;
        this.conctac = conctac;
        this.contactPhone = contactPhone;
        this.comment = comment;
        this.typeAddress = typeAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPickupPointNumber() {
        return pickupPointNumber;
    }

    public void setPickupPointNumber(String pickupPointNumber) {
        this.pickupPointNumber = pickupPointNumber;
    }

    public String getTime_from() {
        return time_from;
    }

    public void setTime_from(String time_from) {
        this.time_from = time_from;
    }

    public String getTime_to() {
        return time_to;
    }

    public void setTime_to(String time_to) {
        this.time_to = time_to;
    }

    public String getGooglePlace() {
        return googlePlace;
    }

    public void setGooglePlace(String googlePlace) {
        this.googlePlace = googlePlace;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getConctac() {
        return conctac;
    }

    public void setConctac(String conctac) {
        this.conctac = conctac;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTypeAddress() {
        return typeAddress;
    }

    public void setTypeAddress(String typeAddress) {
        this.typeAddress = typeAddress;
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
        return "DestinyView{" +
                "pickupPointNumber='" + pickupPointNumber + '\'' +
                ", time_from='" + time_from + '\'' +
                ", time_to='" + time_to + '\'' +
                ", googlePlace='" + googlePlace + '\'' +
                ", distance='" + distance + '\'' +
                ", conctac='" + conctac + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", comment='" + comment + '\'' +
                ", typeAddress='" + typeAddress + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}