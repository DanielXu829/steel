package com.cisdi.steel.dto.response.gl.res;

import java.util.List;

/**
 * Created by w.wolf on 2018/3/16.
 */
public class BatchData {

    public Integer getIndexInDay() {
        return indexInDay;
    }

    public void setIndexInDay(Integer indexInDay) {
        this.indexInDay = indexInDay;
    }

    private Integer indexInDay;

    public BatchIndex getBatchIndex() {
        return batchIndex;
    }

    public void setBatchIndex(BatchIndex batchIndex) {
        this.batchIndex = batchIndex;
    }

    private BatchIndex batchIndex;

    private List<BatchBunker> bunker;
    private List<BatchMaterial> materials;
    private List<BatchDistribution> distributions;

    public List<BatchBunker> getBunker() {
        return bunker;
    }

    public void setBunker(List<BatchBunker> bunker) {
        this.bunker = bunker;
    }

    public List<BatchMaterial> getMaterials() {
        return materials;
    }

    public void setMaterials(List<BatchMaterial> materials) {
        this.materials = materials;
    }

    public List<BatchDistribution> getDistributions() {
        return distributions;
    }

    public void setDistributions(List<BatchDistribution> distributions) {
        this.distributions = distributions;
    }
}
