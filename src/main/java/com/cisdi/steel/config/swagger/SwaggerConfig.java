package com.cisdi.steel.config.swagger;


import io.swagger.annotations.ApiOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * <p>Description: swagger配置类  </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * @author yangpeng
 * @version 1.0
 * @date 2018/1/27
 * @since 1.8
 */
@Configuration
@EnableSwagger2
@ConditionalOnProperty(prefix = "leaf", name = "swagger-open", havingValue = "true")
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //这里采用包含注解的方式来确定要显示的接口
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                //这里采用包扫描的方式来确定要显示的接口
//                .apis(RequestHandlerSelectors.basePackage("com.ypfor.leaf"))
                .paths(PathSelectors.any())
                .build();
    }


    /**
     * api信息
     *
     * @return api信息
     */
    private ApiInfo apiInfo() {
        Contact contact = new Contact("", "", "");
        return new ApiInfoBuilder()
                .title("Leaf doc ")
                .description("Leaf API")
                .termsOfServiceUrl("")
                .contact(contact)
                .version("1.0")
                .build();
    }



}

