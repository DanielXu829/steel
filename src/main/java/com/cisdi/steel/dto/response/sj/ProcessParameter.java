package com.cisdi.steel.dto.response.sj;

import org.apache.ibatis.type.JdbcType;


public class ProcessParameter {

    private Long id;

    private String type;

    private String tagName;

    private String name;

    private String unit;

    private String report;

    private String source;

    private Short orderNum;

    private Short checkFlag;

    private String prodUnit;

    /**
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return TYPE
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return TAG_NAME
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * @param tagName
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    /**
     * @return NAME
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return UNIT
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @return REPORT
     */
    public String getReport() {
        return report;
    }

    /**
     * @param report
     */
    public void setReport(String report) {
        this.report = report;
    }

    /**
     * @return SOURCE
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return ORDER_NUM
     */
    public Short getOrderNum() {
        return orderNum;
    }

    /**
     * @param orderNum
     */
    public void setOrderNum(Short orderNum) {
        this.orderNum = orderNum;
    }

    /**
     * @return CHECK_FLAG
     */
    public Short getCheckFlag() {
        return checkFlag;
    }

    /**
     * @param checkFlag
     */
    public void setCheckFlag(Short checkFlag) {
        this.checkFlag = checkFlag;
    }

    public String getProdUnit() {
        return prodUnit;
    }

    public void setProdUnit(String prodUnit) {
        this.prodUnit = prodUnit;
    }
}