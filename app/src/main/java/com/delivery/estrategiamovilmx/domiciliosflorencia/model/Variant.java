package com.delivery.estrategiamovilmx.domiciliosflorencia.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by administrator on 03/08/2017.
 */
public class Variant implements Serializable {
    @SerializedName("id_variante")
    @Expose
    private String idVariante;
    @SerializedName("id_producto")
    @Expose
    private String idProducto;
    @SerializedName("variante")
    @Expose
    private String variante;
    private final static long serialVersionUID = 4404498337467158567L;

    public String getIdVariante() {
        return idVariante;
    }

    public void setIdVariante(String idVariante) {
        this.idVariante = idVariante;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getVariante() {
        return variante;
    }

    public void setVariante(String variante) {
        this.variante = variante;
    }

    @Override
    public String toString() {
        return new String().concat("idVariante:").concat(idVariante).concat("idProducto:").concat(idProducto).concat("variante:").concat(variante).toString();
    }
}
