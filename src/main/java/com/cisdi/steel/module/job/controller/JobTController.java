package com.cisdi.steel.module.job.controller;


import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.common.util.CookieUtils;
import com.cisdi.steel.common.util.FileUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.ExportJobContext;
import com.cisdi.steel.module.job.a1.execute.ChutiezonglanExecute;
import com.cisdi.steel.module.job.a1.execute.GongyiLuruExecute;
import com.cisdi.steel.module.job.a3.execute.GongyikaExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 报表手动执行相关处理接口
 */
@Slf4j
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
    private GongyikaExecute gongyikaExecute;

    /**
     * 烧结工艺参数导出
     */
    @GetMapping(value = "/gongyiSJExport")
    public void gongyiSJExport(HttpServletRequest request, String id, String code, String save, HttpServletResponse response) {
        try {
            gongyikaExecute.export(request, id, code, save, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private GongyiLuruExecute gongyiLuruExecute;

    /**
     * 高炉工艺参数导出
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
    private static final String COOKIE_NAME = "apiCode";

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
//        try {
//            chutiezonglanExecute.export(request, starttime, endtime, response);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        String cookieValue = CookieUtils.getCookieValue(request, COOKIE_NAME);
        String code = JobEnum.gl_chutiezuoye_day.getCode();
        if ("cms".equals(cookieValue)) {
            code = JobEnum.gl_chutiezuoye6_day.getCode();
        } else if ("cms2".equals(cookieValue)) {
            code = JobEnum.gl_chutiezuoye_day.getCode();
        } else if ("cms3".equals(cookieValue)) {
            code = JobEnum.gl_chutiezuoye7_day.getCode();
        }
        selectAllCategory(code, request, response);
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
    public void selectAllCategory(String code, HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter writer = response.getWriter();
            if (StringUtils.isBlank(code)) {
                writer.write(JSONObject.toJSONString(ApiUtil.fail("编码不能为空")));
            }
            String path = exportJobContext.execute(code);
            if (StringUtils.isNotBlank(path)) {
                FileUtils.downFile(new File(path), request, response, FileUtil.getFileName(path));
            }
        } catch (Exception e) {
            log.error("执行导出失败apiCode=" + code + ">>>>>>>>>>>>>>>>>>" + e.getMessage());
        }
    }
}
