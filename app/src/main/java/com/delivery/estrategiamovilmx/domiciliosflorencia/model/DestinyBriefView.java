package com.delivery.estrategiamovilmx.domiciliosflorencia.model;

public class DestinyBriefView {
    private String pickUpPointNumber;
    private String location;
    private String distance;

    public DestinyBriefView(){}
    public DestinyBriefView(String pickUpPointNumber,String location,String distance){
        this.pickUpPointNumber = pickUpPointNumber;
        this.location = location;
        this.distance = distance;
    }
    public String getPickUpPointNumber() {
        return pickUpPointNumber;
    }

    public void setPickUpPointNumber(String pickUpPointNumber) {
        this.pickUpPointNumber = pickUpPointNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}