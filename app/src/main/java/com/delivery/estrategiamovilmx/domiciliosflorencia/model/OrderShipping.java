package com.delivery.estrategiamovilmx.domiciliosflorencia.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by administrator on 12/07/2017.
 */
public class OrderShipping implements Serializable {
    @SerializedName("origin")
    private PickupAddress origin;
    @SerializedName("destinations")
    private ArrayList<PickupAddress> destinations = new ArrayList<>();
    @SerializedName("packageDetail")
    private PackageDetail packageDetail;
    @SerializedName("total")
    private Float total;
    @SerializedName("roundTrip")
    private boolean roundTrip;
    @SerializedName("originalDestinations")
    private Integer originalDestinations;

    public PickupAddress getOrigin() {
        return origin;
    }

    public void setOrigin(PickupAddress origin) {
        this.origin = origin;
    }

    public ArrayList<PickupAddress> getDestinations() {
        return destinations;
    }

    public void setDestinations(ArrayList<PickupAddress> destinations) {
        this.destinations = destinations;
    }

    public PackageDetail getPackageDetail() {
        return packageDetail;
    }

    public void setPackageDetail(PackageDetail packageDetail) {
        this.packageDetail = packageDetail;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public boolean isRoundTrip() {
        return roundTrip;
    }

    public void setRoundTrip(boolean roundTrip) {
        this.roundTrip = roundTrip;
    }

    public Integer getOriginalDestinations() {
        return originalDestinations;
    }

    public void setOriginalDestinations(Integer originalDestinations) {
        this.originalDestinations = originalDestinations;
    }

    @Override
    public String toString() {
        return "OrderShipping{" +
                "origin=" + origin +
                ", destinations=" + destinations +
                ", packageDetail=" + packageDetail +
                ", total=" + total +
                ", roundTrip=" + roundTrip +
                ", originalDestinations=" + originalDestinations +
                '}';
    }
}
