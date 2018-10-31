package com.cisdi.steel.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * <p>Description: 参数验证的配置 </p>
 * <p>email: ypasdf@163.com </p>
 * <p>Copyright: Copyright (c) 2018 </p>
 * <P>Date: 2018/3/27 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Configuration
public class ValidatorConfiguration {

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        //设置validator模式为快速失败返回
        postProcessor.setValidator(validator());
        return postProcessor;
    }

    @Bean
    public Validator validator(){
        ValidatorFactory validatorFactory = Validation.byProvider( HibernateValidator.class )
                .configure()
                // 快速失败返回模式 true:有一个错误就返回
                // false表示普通模式
                .addProperty( "hibernate.validator.fail_fast", "true" )
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }
}