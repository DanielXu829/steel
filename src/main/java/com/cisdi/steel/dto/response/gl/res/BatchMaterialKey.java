package com.cisdi.steel.dto.response.gl.res;

public class BatchMaterialKey {
    private Long batchno;

    private String brandcode;

    public Long getBatchno() {
        return batchno;
    }

    public void setBatchno(Long batchno) {
        this.batchno = batchno;
    }

    public String getBrandcode() {
        return brandcode;
    }

    public void setBrandcode(String brandcode) {
        this.brandcode = brandcode == null ? null : brandcode.trim();
    }
}