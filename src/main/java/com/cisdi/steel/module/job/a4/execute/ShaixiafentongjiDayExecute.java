package com.cisdi.steel.module.job.a4.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a4.writer.ShaixiafentongjiDayWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 筛下粉统计
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ShaixiafentongjiDayExecute extends AbstractJobExecuteExecute {

    private final ShaixiafentongjiDayWriter shaixiafentongjiDayWriter;

    @Autowired
    public ShaixiafentongjiDayExecute(ShaixiafentongjiDayWriter shaixiafentongjiDayWriter) {
        this.shaixiafentongjiDayWriter = shaixiafentongjiDayWriter;
    }

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return shaixiafentongjiDayWriter;
    }
}
