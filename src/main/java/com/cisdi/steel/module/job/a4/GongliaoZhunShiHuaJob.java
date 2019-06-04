package com.cisdi.steel.module.job.a4;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a4.execute.GongLiaoZhunShiHuaExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 供料准时化横班统计报表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GongliaoZhunShiHuaJob extends AbstractExportJob {

    @Autowired
    private GongLiaoZhunShiHuaExecute gongLiaoZhunShiHuaExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.ygl_gongliaozhunshihua;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return gongLiaoZhunShiHuaExecute;
    }
}
