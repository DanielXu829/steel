package com.cisdi.steel.job;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.job.ExportJobContext;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class ExportJobContextTest extends SteelApplicationTests {

    @Autowired
    private ExportJobContext exportJobContext;

    @Test
    public void execute() {
    }

    /**
     * 重新生成指定生成的报表
     * @throws Exception
     */
    @Test
    public void executeByIndexId() {
        exportJobContext.executeByIndexId(95982L);
    }

    @Test
    public void testExecuteByIndexId() {
        exportJobContext.executeByIndexId(95990L, new Date(1595686200000L));
    }

    @Test
    public void executeByIndexIds() {

    }
}