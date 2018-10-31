package ${package.Mapper};

import org.apache.ibatis.annotations.Mapper;
import ${package.Entity}.${entity};
import ${superMapperClassPackage};

/**
 * <p>Description: ${table.comment} Mapper 接口 </p>
 * <P>Date: ${date} </P>
 *
 * @author ${author}
 * @version 1.0
 */
@Mapper
<#if kotlin>
interface ${table.mapperName} : ${superMapperClass}<${entity}>
<#else>
public interface ${table.mapperName} extends ${superMapperClass}<${entity}> {

}
</#if>
