package com.cisdi.steel.dto.response.gl.res;

import java.util.ArrayList;
import java.util.List;

public class ChargeVarInfo {
    public List<ChargeVarDistribution> getChargeVarDistribution() {
        return chargeVarDistribution;
    }

    public void setChargeVarDistribution(List<ChargeVarDistribution> chargeVarDistribution) {
        this.chargeVarDistribution = chargeVarDistribution;
    }

    public ChargeVarIndex getChargeVarIndex() {
        return chargeVarIndex;
    }

    public void setChargeVarIndex(ChargeVarIndex chargeVarIndex) {
        this.chargeVarIndex = chargeVarIndex;
    }

    public List<ChargeVarMaterial> getChargeVarMaterial() {
        return chargeVarMaterial;
    }

    public void setChargeVarMaterial(List<ChargeVarMaterial> chargeVarMaterial) {
        this.chargeVarMaterial = chargeVarMaterial;
    }

    private List<ChargeVarDistribution> chargeVarDistribution = new ArrayList<>();
    private ChargeVarIndex chargeVarIndex;
    private List<ChargeVarMaterial> chargeVarMaterial = new ArrayList<>();
}
