package com.cisdi.steel.module.report.service.impl;

import com.cisdi.steel.common.exception.LeafException;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.module.report.entity.ReportTemporaryFile;
import com.cisdi.steel.module.report.mapper.ReportTemporaryFileMapper;
import com.cisdi.steel.module.report.service.ReportTemporaryFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class ReportTemporaryFileServiceImpl implements ReportTemporaryFileService {

    @Autowired
    private ReportTemporaryFileMapper reportTemporaryFileMapper;

    @Transactional(rollbackFor = LeafException.class)
    public void deleteAllTemporaryFile() {
        List<ReportTemporaryFile> reportTemporaryFiles = reportTemporaryFileMapper.selectAll();
        reportTemporaryFiles.forEach(file -> {
            FileUtils.deleteFile(file.getFilePath());
            reportTemporaryFileMapper.deleteById(file.getId());
            System.out.println("删除的文件id：" + file.getId());
        });
        log.info("清除临时文件成功");
    }
}
