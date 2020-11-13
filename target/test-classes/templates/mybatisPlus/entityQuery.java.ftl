package ${cfg.queryPackage};

import java.time.LocalDateTime;
<#if entityLombokModel>
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.EqualsAndHashCode;
</#if>
import java.io.Serializable;
import ${cfg.basePackage}.base.vo.PageQuery;

/**
 * <p>Description: ${table.comment} 查询参数(不需要可删除) </p>
 * <P>Date: ${date} </P>
 *
 * @author ${author}
 * @version 1.0
 */
<#if entityLombokModel>
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
</#if>
public class ${entity}Query extends PageQuery implements Serializable {
<#list table.fields as field>
    <#if field.comment!?length gt 0>
    /**
     * ${field.comment}
     */
    </#if>
    private ${field.propertyType} ${field.propertyName};
</#list>
<#------------  END 字段循环遍历  ---------->
}
