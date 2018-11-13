package com.cisdi.steel.config.fastjson;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 95765
 */
@Configuration
public class FastJsonConfiguration extends WebMvcConfigurationSupport {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(fastJsonHttpMessageConverter());
    }

    /**
     * FastJsonHttpMessageConverter
     *
     * @return fastjson配置
     */
    @Bean
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        // 设置fastJson配置
        converter.setFastJsonConfig(fastjsonConfig());
        converter.setSupportedMediaTypes(getSupportedMediaType());
        return converter;
    }


    /**
     * @return fastJson的配置
     */
    private FastJsonConfig fastjsonConfig() {
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        // 策略
        fastJsonConfig.setSerializerFeatures(
                // 格式化
//                SerializerFeature.PrettyFormat,
                // 是否输出值为null的字段,默认为false
//                SerializerFeature.WriteMapNullValue,
                // Enum输出name()或者original,默认为false
//                SerializerFeature.WriteEnumUsingToString,
                // List字段如果为null,输出为[],而非null
//                SerializerFeature.WriteNullListAsEmpty
                //字符类型字段如果为null,输出为"",而非null
//                SerializerFeature.WriteNullStringAsEmpty,
                // 数值字段如果为null,输出为0,而非null
//                SerializerFeature.WriteNullNumberAsZero,
                // Boolean字段如果为null,输出为false,而非null
//                SerializerFeature.WriteNullBooleanAsFalse
        );

//         需要过滤的字段 防止被返回
        Set<String> fields =new HashSet<>();

        fields.add("salt");
        fields.add("createId");
        fields.add("password");
        fields.add("delFlag");
        // 属性过滤
        PropertyFilter proFilter = (object, name, cellValue) -> {
            // 判断是否包含 包含返回false 不包含返回true
            return !fields.contains(name);
        };
        fastJsonConfig.setSerializeFilters(proFilter);

        //解决Long转json精度丢失的问题
        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
        serializeConfig.put(Long.class, ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
        // Swagger2失效的解决
//        serializeConfig.put(Json.class, SwaggerJsonSerializer.instance);
        // 设置是否支持自定类型 用于redis缓存
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        fastJsonConfig.setSerializeConfig(serializeConfig);
        return fastJsonConfig;
    }


    /**
     * 支持的mediaType类型
     *
     * @return 结果
     */
    private List<MediaType> getSupportedMediaType() {
        ArrayList<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        return mediaTypes;
    }
}
