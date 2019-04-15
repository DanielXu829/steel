package com.cisdi.steel.module.job.controller;


import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.common.util.FileUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.ExportJobContext;
import com.cisdi.steel.module.job.a1.execute.ChutiezonglanExecute;
import com.cisdi.steel.module.job.a1.execute.GongyiLuruExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * 报表手动执行相关处理接口
 */
@RestController
@RequestMapping("/job")
public class JobTController {


    @Autowired
    ExportJobContext exportJobContext;

    /**
     * 配料单手动触发执行
     *
     * @param code 配料单任务编码
     * @return
     */
    @GetMapping(value = "/peiLiaoDan")
    public ApiResult peiLiaoDan(String code) {
        if (StringUtils.isBlank(code)) {
            return ApiUtil.fail("编码不能为空");
        }
        //判断执行变料月报数据
        if ("gl_peiliaodan".equals(code)) {
            exportJobContext.execute(JobEnum.gl_gaolubuliao.getCode());
        } else if ("gl_peiliaodan6".equals(code)) {
            exportJobContext.execute(JobEnum.gl_gaolubuliao6.getCode());
        } else if ("gl_peiliaodan7".equals(code)) {
            exportJobContext.execute(JobEnum.gl_gaolubuliao7.getCode());
        }
        String path = exportJobContext.execute(code);
        return ApiUtil.success(path);
    }

    @Autowired
    private GongyiLuruExecute gongyiLuruExecute;

    /**
     * 工艺参数导出
     */
    @GetMapping(value = "/gongyiExport")
    public void gongyiExport(HttpServletRequest request, String time, String code, String save, HttpServletResponse response) {
        try {
            gongyiLuruExecute.export(request, time, code, save, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private ChutiezonglanExecute chutiezonglanExecute;

    /**
     * 出铁总览导出
     *
     * @param starttime
     * @param endtime
     * @param request
     * @param response
     */
    @GetMapping(value = "/chutiezonglan")
    public void chutiezonglan(String starttime, String endtime, HttpServletRequest request, HttpServletResponse response) {
        try {
            chutiezonglanExecute.export(request, starttime, endtime, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 公共报表统一执行导出
     *
     * @param code     报表执行编码
     * @param request
     * @param response
     * @return
     */
    @GetMapping(value = "/export")
    public ApiResult selectAllCategory(String code, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(code)) {
            return ApiUtil.fail("编码不能为空");
        }
        String path = exportJobContext.execute(code);
        if (StringUtils.isNotBlank(path)) {
            try {
                FileUtils.downFile(new File(path), request, response, FileUtil.getFileName(path));
                return null;
            } catch (Exception e) {
                return ApiUtil.fail();
            }
        }
        return ApiUtil.fail();
    }
}
