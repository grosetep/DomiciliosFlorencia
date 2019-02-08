package com.delivery.estrategiamovilmx.domiciliosflorencia.items;


import com.delivery.estrategiamovilmx.domiciliosflorencia.model.Additional;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.Variant;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by administrator on 11/08/2017.
 */
public class GetVariantAdditionalResult implements Serializable {

    @SerializedName("variants")
    @Expose
    private List<Variant> variants = null;
    @SerializedName("additionals")
    @Expose
    private List<Additional> additionals = null;
    private final static long serialVersionUID = 3987473760546464628L;

    public List<Variant> getVariants() {
        return variants;
    }

    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }

    public List<Additional> getAdditionals() {
        return additionals;
    }

    public void setAdditionals(List<Additional> additionals) {
        this.additionals = additionals;
    }

    @Override
    public String toString() {
        return new String().concat("variants:").concat(""+variants.size()).concat("additionals:").concat(""+additionals.size()).toString();
    }
}
