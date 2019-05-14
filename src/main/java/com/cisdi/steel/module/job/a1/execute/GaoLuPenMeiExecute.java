package com.cisdi.steel.module.job.a1.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a1.writer.GaoLuPenMeiWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 高炉喷煤
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GaoLuPenMeiExecute extends AbstractJobExecuteExecute {


    @Autowired
    private GaoLuPenMeiWriter gaoLuPenMeiWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return gaoLuPenMeiWriter;
    }
}
