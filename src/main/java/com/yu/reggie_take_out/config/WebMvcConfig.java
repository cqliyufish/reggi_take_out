package com.yu.reggie_take_out.config;

import com.yu.reggie_take_out.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 设置静态资源映射（如果不放在static/templates目录下，需要使用）
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        //classpath对于resources
        log.info("开始映射");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * 扩展MVC框架消息转换器，将Long转化为String，防止JS精度不够
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters){

        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter= new MappingJackson2HttpMessageConverter();
        //设置对象转换器
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //追加到MVC转换器集合中 index=0 优先使用
        converters.add(0,messageConverter);
    }


}
