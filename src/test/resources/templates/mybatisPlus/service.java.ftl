package ${package.Service};

import ${package.Entity}.${entity};
import ${superServiceClassPackage};

/**
 * <p>Description: ${table.comment} 服务类 </p>
 * <P>Date: ${date} </P>
 *
 * @author ${author}
 * @version 1.0
 */
<#if kotlin>
interface ${table.serviceName} : ${superServiceClass}<${entity}>
<#else>
public interface ${table.serviceName} extends ${superServiceClass}<${entity}> {

}
</#if>
