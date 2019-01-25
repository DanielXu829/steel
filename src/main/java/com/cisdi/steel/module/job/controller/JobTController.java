package com.cisdi.steel.module.job.controller;


import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.common.util.FileUtil;
import com.cisdi.steel.common.util.FileUtils;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.ExportJobContext;
import com.cisdi.steel.module.job.a1.execute.ChutiezonglanExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/job")
public class JobTController {


    @Autowired
    ExportJobContext exportJobContext;


    @GetMapping(value = "/peiLiaoDan")
    public ApiResult peiLiaoDan(String code) {
        if (StringUtils.isBlank(code)) {
            return ApiUtil.fail("编码不能为空");
        }
        String path = exportJobContext.execute(code);
        return ApiUtil.success(path);
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
