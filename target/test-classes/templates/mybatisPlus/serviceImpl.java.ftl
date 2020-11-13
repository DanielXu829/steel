package ${package.ServiceImpl};

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import ${cfg.basePackage}.resp.ApiUtil;
import ${cfg.basePackage}.resp.ApiResult;
import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
import ${superServiceImplClassPackage};
/**
 * <p>Description: ${table.comment} 服务实现类 </p>
 * <P>Date: ${date} </P>
 *
 * @author ${author}
 * @version 1.0
 */
@Service
<#if kotlin>
open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>(), ${table.serviceName} {

}
<#else>
public class ${table.serviceImplName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}> implements ${table.serviceName} {



}
</#if>
