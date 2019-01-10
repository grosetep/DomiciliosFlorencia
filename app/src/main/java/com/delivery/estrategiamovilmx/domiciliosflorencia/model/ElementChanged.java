package com.delivery.estrategiamovilmx.domiciliosflorencia.model;

import java.io.Serializable;

/**
 * Created by administrator on 03/08/2017.
 */
public class ElementChanged implements Serializable{
    private int indexElement;
    private boolean isNew;
    private boolean changed;

    public int getIndexElement() {
        return indexElement;
    }

    public void setIndexElement(int indexElement) {
        this.indexElement = indexElement;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
