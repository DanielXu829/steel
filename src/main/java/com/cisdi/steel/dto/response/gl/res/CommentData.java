package com.cisdi.steel.dto.response.gl.res;

public class CommentData extends CommentDataKey {
    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}