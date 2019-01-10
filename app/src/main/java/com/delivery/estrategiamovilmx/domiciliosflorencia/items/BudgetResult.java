package com.delivery.estrategiamovilmx.domiciliosflorencia.items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by administrator on 09/08/2017.
 */
public class BudgetResult implements Serializable {
    @SerializedName("total_destinations")
    @Expose
    private Integer totalDestinations;
    @SerializedName("is_valid_delivery")
    @Expose
    private Boolean isValidDelivery;
    @SerializedName("total")
    @Expose
    private Double total;
    @SerializedName("distances")
    @Expose
    private List<String> distances = null;
    @SerializedName("costs")
    @Expose
    private List<Float> costs = null;
    @SerializedName("totalDistance")
    @Expose
    private String totalDistance;
    @SerializedName("originalDestinations")
    @Expose
    private Integer originalDestinations;


    private final static long serialVersionUID = -2435552350770474540L;

    public Integer getTotalDestinations() {
        return totalDestinations;
    }

    public void setTotalDestinations(Integer totalDestinations) {
        this.totalDestinations = totalDestinations;
    }

    public Boolean getIsValidDelivery() {
        return isValidDelivery;
    }

    public void setIsValidDelivery(Boolean isValidDelivery) {
        this.isValidDelivery = isValidDelivery;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<String> getDistances() {
        return distances;
    }

    public void setDistances(List<String> distances) {
        this.distances = distances;
    }

    public List<Float> getCosts() {
        return costs;
    }

    public void setCosts(List<Float> costs) {
        this.costs = costs;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Integer getOriginalDestinations() {
        return originalDestinations;
    }

    public void setOriginalDestinations(Integer originalDestinations) {
        this.originalDestinations = originalDestinations;
    }

    @Override
    public String toString() {
        return "BudgetResult{" +
                "totalDestinations=" + totalDestinations +
                ", isValidDelivery=" + isValidDelivery +
                ", total=" + total +
                ", distances=" + distances +
                ", costs=" + costs +
                ", totalDistance='" + totalDistance + '\'' +
                ", originalDestinations=" + originalDestinations +
                '}';
    }
}
