package com.cisdi.steel.dto.response.gl.res;

import java.util.Date;

public class CommentDataKey {
    private String model;

    private Short id;

    private Date clock;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model == null ? null : model.trim();
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public Date getClock() {
        return clock;
    }

    public void setClock(Date clock) {
        this.clock = clock;
    }
}