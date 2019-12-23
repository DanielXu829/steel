package com.cisdi.steel.module.job.gl.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.gl.writer.ChuTieHuaXueChengFenWriter;
import com.cisdi.steel.module.job.gl.writer.ShangLiaoZhuangLiaoWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 上料装料 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/22 </P>
 *
 * @version 1.0
 */
@Component
public class ShangLiaoZhuangLiaoExecute extends AbstractJobExecuteExecute {

    @Autowired
    private ShangLiaoZhuangLiaoWriter shangLiaoZhuangLiaoWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return shangLiaoZhuangLiaoWriter;
    }
}
