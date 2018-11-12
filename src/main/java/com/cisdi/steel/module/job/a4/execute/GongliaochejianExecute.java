package com.cisdi.steel.module.job.a4.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a4.writer.GongliaochejianWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 供料车间集控中心交接班记录
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GongliaochejianExecute extends AbstractJobExecuteExecute {

    private final GongliaochejianWriter gongliaochejianWriter;

    @Autowired
    public GongliaochejianExecute(GongliaochejianWriter gongliaochejianWriter) {
        this.gongliaochejianWriter = gongliaochejianWriter;
    }

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return gongliaochejianWriter;
    }
}
