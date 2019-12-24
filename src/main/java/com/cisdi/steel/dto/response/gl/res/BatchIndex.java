package com.cisdi.steel.dto.response.gl.res;

import java.util.Date;

public class BatchIndex {
    private Long batchno;

    private Long serialno;

    private Date weighttime;

    private Date starttime;

    private Date endtime;

    private String typ;

    private Long chargeno;

    private Long matrixno;

    public Long getBatchno() {
        return batchno;
    }

    public void setBatchno(Long batchno) {
        this.batchno = batchno;
    }

    public Long getSerialno() {
        return serialno;
    }

    public void setSerialno(Long serialno) {
        this.serialno = serialno;
    }

    public Date getWeighttime() {
        return weighttime;
    }

    public void setWeighttime(Date weighttime) {
        this.weighttime = weighttime;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ == null ? null : typ.trim();
    }

    public Long getChargeno() {
        return chargeno;
    }

    public void setChargeno(Long chargeno) {
        this.chargeno = chargeno;
    }

    public Long getMatrixno() {
        return matrixno;
    }

    public void setMatrixno(Long matrixno) {
        this.matrixno = matrixno;
    }
}