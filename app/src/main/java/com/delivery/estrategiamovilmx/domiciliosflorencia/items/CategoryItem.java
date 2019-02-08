package com.delivery.estrategiamovilmx.domiciliosflorencia.items;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by administrator on 09/08/2017.
 */
public class CategoryItem implements Serializable {
    @SerializedName("idCategory")
    @Expose
    private String idCategory;
    @SerializedName("category")
    @Expose
    private String category;
    private final static long serialVersionUID = -6382614353627624762L;

    public String getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(String idCategory) {
        this.idCategory = idCategory;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return new String().concat("idCategoria:").concat(idCategory).concat("categoria:").concat(category).toString();
    }
}