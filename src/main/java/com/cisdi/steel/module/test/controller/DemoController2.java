package com.cisdi.steel.module.test.controller;

import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.module.test.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * @author Administrator
 */
@RestController
public class DemoController2 {


    @Autowired
    private DemoService demoService;

    @RequestMapping("/onlyoffice/demo")
    public ModelAndView demo(Map<String, Object> map, @RequestParam(value = "filePath", defaultValue = "/root/office/") String filePath) {
        map.put("list", demoService.fileListDirectory(filePath));
        return new ModelAndView("demo");
    }

    @RequestMapping("/onlyoffice/open")
    public ApiResult open(@RequestParam(value = "filePath", defaultValue = "/root/office/") String filePath) {
        return ApiUtil.success(demoService.fileListDirectory(filePath));
    }

    @RequestMapping("/onlyoffice/edit")
    public ApiResult edit(@RequestParam(value = "filePath", defaultValue = "/root/office/") String filePath) {
        return ApiUtil.success(demoService.fileListDirectory(filePath));
    }
}
