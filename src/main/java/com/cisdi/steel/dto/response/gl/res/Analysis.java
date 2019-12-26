package com.cisdi.steel.dto.response.gl.res;

import java.util.Date;

public class Analysis {
    private Integer anaid;

    private String brandcode;

    private Date clock;

    private Date sampletime;

    private Short integral;

    private String type;

    private Short bfno;

    private Short iscorrect;

    private String sampleid;

    public Integer getAnaid() {
        return anaid;
    }

    public void setAnaid(Integer anaid) {
        this.anaid = anaid;
    }

    public String getBrandcode() {
        return brandcode;
    }

    public void setBrandcode(String brandcode) {
        this.brandcode = brandcode == null ? null : brandcode.trim();
    }

    public Date getClock() {
        return clock;
    }

    public void setClock(Date clock) {
        this.clock = clock;
    }

    public Date getSampletime() {
        return sampletime;
    }

    public void setSampletime(Date sampletime) {
        this.sampletime = sampletime;
    }

    public Short getIntegral() {
        return integral;
    }

    public void setIntegral(Short integral) {
        this.integral = integral;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Short getBfno() {
        return bfno;
    }

    public void setBfno(Short bfno) {
        this.bfno = bfno;
    }

    public Short getIscorrect() {
        return iscorrect;
    }

    public void setIscorrect(Short iscorrect) {
        this.iscorrect = iscorrect;
    }

    public String getSampleid() {
        return sampleid;
    }

    public void setSampleid(String sampleid) {
        this.sampleid = sampleid == null ? null : sampleid.trim();
    }
}