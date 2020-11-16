package com.cisdi.steel.module.report.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.base.vo.BaseId;
import com.cisdi.steel.common.base.vo.PageQuery;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.config.http.OkHttpUtil;
import com.cisdi.steel.module.report.entity.TargetManagement;
import io.swagger.annotations.ResponseHeader;
import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import com.cisdi.steel.module.report.service.ReportTemplateTagsService;
import com.cisdi.steel.module.report.entity.ReportTemplateTags;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * <p>Description: 报表动态模板 - 参数列表 前端控制器 </p>
 * <P>Date: 2019-12-11 </P>
 *
 * @author cisdi
 * @version 1.0
 */
@RestController
@RequestMapping("/reportTemplateTags")
public class ReportTemplateTagsController {

    /**
     * 构造器注入
     */
    private final ReportTemplateTagsService baseService;

    @Autowired
    public ReportTemplateTagsController(ReportTemplateTagsService baseService) {
        this.baseService = baseService;
    }
    /**
     * 列表
     */
    @PostMapping(value = "/pageList")
    public ApiResult pageList(@RequestBody PageQuery query) {
        return baseService.pageList(query);
    }

    /**
     * 插入
     */
    @PostMapping(value = "/insert")
    public ApiResult insertRecord(@RequestBody ReportTemplateTags record) {
        return baseService.insertRecord(record);
    }

    /**
     * 更新
     */
    @PostMapping(value = "/update")
    public ApiResult updateRecord(@RequestBody ReportTemplateTags record) {
        return baseService.updateRecord(record);
    }

    /**
     * 查询
     */
    @PostMapping(value = "/get")
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

    @PostMapping(value = "/mgReport")

    public List<String> test(@RequestParam String sheetId) {
        return baseService.selectTagNameBySheetId(sheetId);
    }
    @PostMapping(value="/param")
    public ApiResult test1(@RequestParam String sheetId){
        return baseService.test1(sheetId);
    }
    @PostMapping("/report")
    public ApiResult selectTagNameBySheetId(@RequestParam String sheetId,@RequestParam String starttime,@RequestParam String endtime){
        List<String> names = baseService.selectTagNameBySheetId(sheetId);
        HashMap<String,Object> map = new HashMap<String,Object>();
        String[] arr =  new String[names.size()];
        if(names !=null && names.size()>0){
            for (int i = 0 ; i < names.size() ; i++){
                arr[i] = names.get(i);
            }
        }
        map.put("tagnames",arr);
        map.put("starttime",starttime);
        map.put("endtime",endtime);
        String str2 = JSON.toJSONString(map);
        String result = OkHttpUtil.postJsonParams("http://119.84.70.208:92/bf2/getTagValues/tagNamesInRange/report",str2);
        JSONObject resultjsonObject  =(JSONObject) JSONObject.parse(result);
        ApiResult<List<TargetManagement>> str3 = baseService.test1(sheetId);
        resultjsonObject.put("dataMap",str3);
        return ApiUtil.success(resultjsonObject);
    }
}
