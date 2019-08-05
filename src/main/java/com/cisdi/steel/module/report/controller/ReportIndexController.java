package com.cisdi.steel.module.report.controller;

import com.cisdi.steel.common.base.vo.BaseId;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.FileUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.config.http.HttpUtil;
import com.cisdi.steel.module.job.ExportJobContext;
import com.cisdi.steel.module.job.config.HttpProperties;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.report.dto.ReportPathDTO;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.query.ReportIndexQuery;
import com.cisdi.steel.module.report.service.ReportIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

/**
 * <p>Description: 报表文件-索引 前端控制器 </p>
 * <P>Date: 2018-10-24 </P>
 *
 * @author leaf
 * @version 1.0
 */
@RestController
@RequestMapping("/report/reportIndex")
public class ReportIndexController {
    @Autowired
    protected HttpUtil httpUtil;
    @Autowired
    protected HttpProperties httpProperties;

    /**
     * 构造器注入
     */
    private final ReportIndexService baseService;

    private final ExportJobContext exportJobContext;

    @Autowired
    public ReportIndexController(ReportIndexService baseService, ExportJobContext exportJobContext) {
        this.baseService = baseService;
        this.exportJobContext = exportJobContext;
    }

    /**
     * 列表
     */
    @PostMapping(value = "/pageList")
    public ApiResult pageList(@RequestBody ReportIndexQuery query) {
        return baseService.pageList(query);
    }

    /**
     * 更新
     */
    @PostMapping(value = "/update")
    public ApiResult updateRecord(@RequestBody ReportIndex record) {
        return baseService.updateRecord(record);
    }

    /**
     * 查询
     */
    @PostMapping(value = "/detail")
    public ApiResult getRecord(@RequestBody BaseId baseId) {
        return baseService.getById(baseId.getId());
    }

    /**
     * 删除
     */
    @PostMapping(value = "/delete")
    public ApiResult deleteById(@RequestBody BaseId baseId) {
        ReportIndex report = baseService.getById(baseId);
        if(StringUtils.isNotBlank(report.getPath())){
            FileUtils.deleteFile(report.getPath());
        }

        String code = report.getReportCategoryCode();
        Date createTime = report.getCreateTime();
        String version = "";
        List<String> tagNames = new ArrayList<>();
        if(JobEnum.jh_zidongpeimei.getCode().equals(code)){
            version = "67.0";
            tagNames.add("CK67_L1R_CB_CBAmtTol_1m_evt");
        } else if(JobEnum.jh_ck12zidongpeimeinew.getCode().equals(code)){
            version = "12.0";
            tagNames.add("CK12_L1R_CB_CBAmtTol1_evt");
            tagNames.add("CK12_L1R_CB_CBAmtTol2_evt");
        } else if(JobEnum.jh_ck45zidongpeimei.getCode().equals(code)){
            version = "45.0";
            tagNames.add("CK45_L1R_CB_CBAmtTol_evt");
        }

        String url = httpProperties.getJHUrlVersion(version) + "/jhTagValue/deleteTagValueByDate";
        // 去删除对应时间的班总配煤量evt点的值
        Map<String, String> map = new HashMap<>();
        map.put("startTime", DateUtil.getFormatDateTime(DateUtil.addMinute(createTime, -2), "yyyy/MM/dd HH:mm:ss"));
        map.put("endTime", DateUtil.getFormatDateTime(DateUtil.addMinute(createTime, 1), "yyyy/MM/dd HH:mm:ss"));
        for (String tagName : tagNames) {
            map.put("tagName", tagName);
            httpUtil.get(url, map);
        }

        return baseService.deleteRecord(baseId);
    }

    /**
     * 下载
     *
     * @param reportPathDTO 路径
     */
    @RequestMapping(value = "/download")
    public void deleteById(ReportPathDTO reportPathDTO, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(reportPathDTO.getPath())) {
            return;
        }
        FileUtils.downFile(new File(reportPathDTO.getPath()), request, response, FileUtil.getFileName(reportPathDTO.getPath()));
    }

    /**
     * 文件上传
     *
     * @param file 需要上传的文件
     * @return 成功不反回错误信息
     */
    @PostMapping(value = "/upload")
    public ApiResult upload(MultipartFile file) {
        if (Objects.isNull(file) || file.getSize() < 0) {
            return ApiUtil.fail("文件不能为空");
        }
        return baseService.upload(file);
    }

    /**
     * 首页报表数据
     *
     * @return 报表首页需要的展示数据封装结果
     */
    @RequestMapping(value = "/index")
    public ApiResult index(ReportIndexQuery reportIndexQuery) {
        return baseService.reportIndex(reportIndexQuery);
    }

    /**
     * 重新生成指定报表
     *
     * @return 报表首页需要的展示数据封装结果
     */
    @PostMapping(value = "/reloadIndex")
    public ApiResult reloadIndex(@RequestBody ReportIndexQuery reportIndexQuery) {
        if (Objects.isNull(reportIndexQuery.getReportDate())) {
            exportJobContext.executeByIndexId(reportIndexQuery.getId());
        } else {
            exportJobContext.executeByIndexId(reportIndexQuery.getId(), reportIndexQuery.getReportDate());
        }
        return ApiUtil.success();
    }

    /**
     * 对已经生成的报表批量生成
     *
     * @return 报表首页需要的展示数据封装结果
     */
    @PostMapping(value = "/reloadIndexAll")
    public ApiResult reloadIndexAll(@RequestBody ReportIndexQuery reportIndexQuery) {
        if (reportIndexQuery.getId().intValue() != -2) {
            return ApiUtil.fail();
        }
        exportJobContext.executeByIndexIds(reportIndexQuery.getId());
        return ApiUtil.success();
    }

}
