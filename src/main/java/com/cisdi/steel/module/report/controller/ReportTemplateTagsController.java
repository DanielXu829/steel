package com.cisdi.steel.module.report.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import com.cisdi.steel.module.report.service.ReportTemplateTagsService;
import com.cisdi.steel.module.report.entity.ReportTemplateTags;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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

    /*@Value("${http.url.urlApiSJThree}")
    String url = "http://10.21.41.27:92/bf2/getTagValues/tagNamesInRange/report";*/

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
    public List<String> test1(@RequestParam String sheetId){
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
        String result = OkHttpUtil.postJsonParams("http://10.21.41.27:92/bf2/getTagValues/tagNamesInRange/report",str2);
        JSONObject resultjsonObject  =(JSONObject) JSONObject.parse(result);
        JSONArray dataArray = resultjsonObject.getJSONArray("data");
        return ApiUtil.success(dataArray);
    }

    /**
     * 将data中的val和clock根据name对dataMap中的target_formula进行分组，
     * 分别作为一个字段追加到对应的dataMap中。
     * 最后将多余的data删除
     * @param resultjsonObject 包含data和dataMap的json对象
     */
    private void groupValueByName(JSONObject resultjsonObject) {
        JSONArray titleJsonArray = resultjsonObject.getJSONArray("dataMap");
        JSONArray dataJsonArray = resultjsonObject.getJSONArray("data");
        for (Object o : titleJsonArray) {
            JSONObject titleJsonObject = (JSONObject) o;
            String targetFormula = titleJsonObject.getString("target_formula");

            appendNewField(dataJsonArray, titleJsonObject, targetFormula);
        }
        resultjsonObject.remove("data");
        resultjsonObject.put("dataMap", titleJsonArray);
    }

    private void appendNewField(JSONArray dataJsonArray, JSONObject titleJsonObject, String targetFormula) {
        List<Double> dataList = new ArrayList<>();
        List<Long> clockList = new ArrayList<>();
        for (Object o1 : dataJsonArray) {
            JSONObject data = (JSONObject) o1;
            if (data.getString("name").equals(targetFormula)) {
                dataList.add(data.getDouble("val"));
                clockList.add(data.getLong("clock"));
            }
        }
        titleJsonObject.put("dataList", dataList);
        titleJsonObject.put("clockList", clockList);
    }


}
