package ${package.Controller};

import ${cfg.basePackage}.resp.ApiResult;
import ${cfg.basePackage}.base.vo.BaseId;
import ${cfg.basePackage}.base.vo.PageQuery;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import ${package.Service}.${table.serviceName};
import ${package.Entity}.${entity};
<#if restControllerStyle>
import org.springframework.web.bind.annotation.RestController;
<#else>
import org.springframework.stereotype.Controller;
</#if>
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>

/**
 * <p>Description: ${table.comment} 前端控制器 </p>
 * <P>Date: ${date} </P>
 *
 * @author ${author}
 * @version 1.0
 */
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
@RequestMapping("<#if package.ModuleName??>/${package.ModuleName}</#if>/<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")
<#if kotlin>
class ${table.controllerName}<#if superControllerClass??> : ${superControllerClass}()</#if>
<#else>
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
<#else>
public class ${table.controllerName} {

    /**
     * 构造器注入
     */
    private final ${table.serviceName} baseService;

    @Autowired
    public ${table.controllerName}(${table.serviceName} baseService) {
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
    public ApiResult insertRecord(@RequestBody ${entity} record) {
        return baseService.insertRecord(record);
    }

    /**
     * 更新
     */
    @PostMapping(value = "/update")
    public ApiResult updateRecord(@RequestBody ${entity} record) {
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

}
</#if>
</#if>
