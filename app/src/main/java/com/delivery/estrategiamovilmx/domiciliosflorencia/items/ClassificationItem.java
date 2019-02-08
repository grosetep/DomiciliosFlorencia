package com.delivery.estrategiamovilmx.domiciliosflorencia.items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ClassificationItem implements Serializable {
    @SerializedName("serviceKey")
    @Expose
    private String serviceKey;
    @SerializedName("classificationKey")
    @Expose
    private String classificationKey;
    @SerializedName("classification")
    @Expose
    private String classification;
    @SerializedName("pathIcon")
    @Expose
    private String pathIcon;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("selected")
    @Expose
    private String selected;

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public String getClassificationKey() {
        return classificationKey;
    }

    public void setClassificationKey(String classificationKey) {
        this.classificationKey = classificationKey;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getPathIcon() {
        return pathIcon;
    }

    public void setPathIcon(String pathIcon) {
        this.pathIcon = pathIcon;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "ClassificationItem{" +
                "serviceKey='" + serviceKey + '\'' +
                ", classificationKey='" + classificationKey + '\'' +
                ", classification='" + classification + '\'' +
                ", pathIcon='" + pathIcon + '\'' +
                ", icon='" + icon + '\'' +
                ", selected='" + selected + '\'' +
                '}';
    }
}