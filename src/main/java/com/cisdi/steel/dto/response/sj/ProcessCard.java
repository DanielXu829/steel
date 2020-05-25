package com.cisdi.steel.dto.response.sj;

import org.apache.ibatis.type.JdbcType;
import java.util.Date;

public class ProcessCard {
    private Long id;

    private Date recordDate;

    private String recorder;

    private Date startTime;

    private String nightSignature;

    private String daySignature;

    private String middleSignature;

    private String note;

    private Date endTime;

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
     * @return RECORD_DATE
     */
    public Date getRecordDate() {
        return recordDate;
    }

    /**
     * @param recordDate
     */
    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    /**
     * @return RECORDER
     */
    public String getRecorder() {
        return recorder;
    }

    /**
     * @param recorder
     */
    public void setRecorder(String recorder) {
        this.recorder = recorder;
    }

    /**
     * @return START_TIME
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return NIGHT_SIGNATURE
     */
    public String getNightSignature() {
        return nightSignature;
    }

    /**
     * @param nightSignature
     */
    public void setNightSignature(String nightSignature) {
        this.nightSignature = nightSignature;
    }

    /**
     * @return DAY_SIGNATURE
     */
    public String getDaySignature() {
        return daySignature;
    }

    /**
     * @param daySignature
     */
    public void setDaySignature(String daySignature) {
        this.daySignature = daySignature;
    }

    /**
     * @return MIDDLE_SIGNATURE
     */
    public String getMiddleSignature() {
        return middleSignature;
    }

    /**
     * @param middleSignature
     */
    public void setMiddleSignature(String middleSignature) {
        this.middleSignature = middleSignature;
    }

    /**
     * @return NOTE
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note
     */
    public void setNote(String note) {
        this.note = note;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getProdUnit() {
        return prodUnit;
    }

    public void setProdUnit(String prodUnit) {
        this.prodUnit = prodUnit;
    }
}