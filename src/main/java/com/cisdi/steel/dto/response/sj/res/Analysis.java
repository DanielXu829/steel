package com.cisdi.steel.dto.response.sj.res;

import lombok.Data;

import java.util.Date;

@Data
public class Analysis {
    private Integer anaid;

    private String sampleid;

    private Date sampletime;

    private Date clock;

    private String brandcode;

    private Short integral;

    private Short iscorrect;

    private String type;

    private String note;

    private String prodUnitCode;

    private String samplePosCode;

    private String matPileNo;

    private String workShift;

    private String workTeam;

    private String supplierCode;

    private String brandName;
}