package com.cisdi.steel.module.job.a4.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a4.writer.MeitouwaipaiMonthWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class MeitouwaipaiMonthExecute extends AbstractJobExecuteExecute {

    private final MeitouwaipaiMonthWriter meitouwaipaiMonthWriter;

    @Autowired
    public MeitouwaipaiMonthExecute(MeitouwaipaiMonthWriter meitouwaipaiMonthWriter) {
        this.meitouwaipaiMonthWriter = meitouwaipaiMonthWriter;
    }

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return meitouwaipaiMonthWriter;
    }
}
