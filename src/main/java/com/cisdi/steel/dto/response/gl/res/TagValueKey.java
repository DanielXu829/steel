package com.cisdi.steel.dto.response.gl.res;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class TagValueKey {
    private Integer id;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date clock;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getClock() {
        return clock;
    }

    public void setClock(Date clock) {
        this.clock = clock;
    }
}