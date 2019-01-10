package com.delivery.estrategiamovilmx.domiciliosflorencia.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ShippingServiceModel implements Parcelable{
    private String image;
    private String key;
    private String label;

    public static final Creator CREATOR = new Creator() {
        public ShippingServiceModel createFromParcel(Parcel in) {
            return new ShippingServiceModel(in);
        }

        public ShippingServiceModel[] newArray(int size) {
            return new ShippingServiceModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
    // Parcelling part
    public ShippingServiceModel(Parcel in){
        this.image = in.readString();
        this.key = in.readString();
        this.label =  in.readString();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "ShippingServiceModel{" +
                "image='" + image + '\'' +
                ", key='" + key + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}