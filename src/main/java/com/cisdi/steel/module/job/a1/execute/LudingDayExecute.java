package com.cisdi.steel.module.job.a1.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a1.readwriter.LudingReadWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 炉顶作业日报 执行的类
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/6 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
@Slf4j
public class LudingDayExecute extends AbstractJobExecuteExecute {

    @Autowired
    private LudingReadWriter ludingReadWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return ludingReadWriter;
    }
}
