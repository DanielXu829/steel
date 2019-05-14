package com.cisdi.steel.module.sys.controller;

import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.sys.entity.SysConfig;
import com.cisdi.steel.module.sys.query.SysConfigQuery;
import com.cisdi.steel.module.sys.service.SysConfigService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Description: 系统配置 前端控制器 </p>
 * <P>Date: 2018-08-22 </P>
 *
 * @author leaf
 * @version 1.0
 */
@RestController
@Api(description = "a")
@RequestMapping("/sys/sysConfig")
public class SysConfigController {

    /**
     * 构造器注入
     */
    private final SysConfigService baseService;

    @Autowired
    public SysConfigController(SysConfigService baseService) {
        this.baseService = baseService;
    }

    /**
     * 列表
     */
    @PostMapping(value = "/pageList")
    public ApiResult pageList(@RequestBody SysConfigQuery sysConfigQuery) {
        return baseService.pageList(sysConfigQuery);
    }


    /**
     * 更新
     */
    @PostMapping(value = "/update")
    public ApiResult<SysConfig> updateRecord(@RequestBody SysConfig record) {
        if (StringUtils.isBlank(record.getCode()) || StringUtils.isBlank(record.getAction())) {
            return null;
        }
        return baseService.updateRecord(record);
    }


    /**
     * 系统参数
     */
    @PostMapping(value = "/system")
    public ApiResult systemParam() {
        return baseService.systemParam();
    }
}
