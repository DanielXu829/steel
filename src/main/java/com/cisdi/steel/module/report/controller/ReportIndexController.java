package com.cisdi.steel.module.report.controller;

import com.cisdi.steel.common.base.vo.BaseId;
import com.cisdi.steel.common.base.vo.PageQuery;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.common.util.CookieUtils;
import com.cisdi.steel.common.util.FileUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.ExportJobContext;
import com.cisdi.steel.module.report.dto.ReportPathDTO;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.query.ReportIndexQuery;
import com.cisdi.steel.module.report.service.ReportIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Objects;

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
        exportJobContext.executeByIndexId(reportIndexQuery.getId());
        return ApiUtil.success();
    }

}
