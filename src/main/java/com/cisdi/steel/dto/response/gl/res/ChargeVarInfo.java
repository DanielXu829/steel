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


    public double getTheroyHMMass() {
        return theroyHMMass;
    }

    public void setTheroyHMMass(double theroyHMMass) {
        this.theroyHMMass = theroyHMMass;
    }

    public double getCokeRate() {
        return cokeRate;
    }

    public void setCokeRate(double cokeRate) {
        this.cokeRate = cokeRate;
    }

    public double getCokeMass() {
        return cokeMass;
    }

    public void setCokeMass(double cokeMass) {
        this.cokeMass = cokeMass;
    }

    public double getOreMass() {
        return oreMass;
    }

    public void setOreMass(double oreMass) {
        this.oreMass = oreMass;
    }


    private List<ChargeVarDistribution> chargeVarDistribution = new ArrayList<>();
    private ChargeVarIndex chargeVarIndex;
    private List<ChargeVarMaterial> chargeVarMaterial = new ArrayList<>();
    private double oreMass = 0;
    private double theroyHMMass = 0;
    private double cokeRate = 0;
    private double cokeMass = 0;

    public double getLumporeRate() {
        return lumporeRate;
    }

    public void setLumporeRate(double lumporeRate) {
        this.lumporeRate = lumporeRate;
    }

    public double getSinterRate() {
        return sinterRate;
    }

    public void setSinterRate(double sinterRate) {
        this.sinterRate = sinterRate;
    }

    public double getPelletsRate() {
        return pelletsRate;
    }

    public void setPelletsRate(double pelletsRate) {
        this.pelletsRate = pelletsRate;
    }

    private double lumporeRate = 0;
    private double sinterRate = 0;

    public double getCokeLoad() {
        return cokeLoad;
    }

    public void setCokeLoad(double cokeLoad) {
        this.cokeLoad = cokeLoad;
    }

    private double pelletsRate = 0;
    private double cokeLoad = 0;

}
