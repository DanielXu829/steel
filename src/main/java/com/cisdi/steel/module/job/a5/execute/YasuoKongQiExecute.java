package com.cisdi.steel.module.job.a5.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a5.writer.YasuoKongQiWriter;
import com.cisdi.steel.module.job.dto.ExcelPathInfo;
import com.cisdi.steel.module.job.dto.JobExecuteInfo;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 压缩空气生产情况汇总表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class YasuoKongQiExecute extends AbstractJobExecuteExecute {

    @Autowired
    private YasuoKongQiWriter yasuoKongQiWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return yasuoKongQiWriter;
    }

    @Override
    public void execute(JobExecuteInfo jobExecuteInfo) {
        super.execute(jobExecuteInfo);
        //生成昨天的
//        super.executeDateParam(jobExecuteInfo, -1);
    }

    @Override
    public void createFile(Workbook workbook, ExcelPathInfo excelPathInfo, WriterExcelDTO writerExcelDTO, DateQuery dateQuery) throws IOException {
        super.createFile(workbook, excelPathInfo, writerExcelDTO, dateQuery);
    }
}
