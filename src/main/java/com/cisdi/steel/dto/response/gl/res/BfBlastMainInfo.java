package com.cisdi.steel.dto.response.gl.res;

import java.util.List;

public class BfBlastMainInfo {
    public BfBlastResult getBfBlastResult() {
        return bfBlastResult;
    }

    public void setBfBlastResult(BfBlastResult bfBlastResult) {
        this.bfBlastResult = bfBlastResult;
    }

    public List<BfBlastMain> getBfBlastMains() {
        return bfBlastMains;
    }

    public void setBfBlastMains(List<BfBlastMain> bfBlastMains) {
        this.bfBlastMains = bfBlastMains;
    }

    BfBlastResult bfBlastResult;
    List<BfBlastMain> bfBlastMains;

}
