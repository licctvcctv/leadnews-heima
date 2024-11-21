package com.heima.wemedia.config;

import com.heima.wemedia.interceptor.WmTokeninterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration 类中的 @Bean 方法会被 Spring 代理，以确保它们总是返回相同的实例。
@Configuration // 声明该类为配置类，Spring 会自动扫描并加载该类

public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {// 注册自定义拦截器 WmTokeninterceptor，并指定拦截的路径模式

        registry.addInterceptor(new WmTokeninterceptor())
                .addPathPatterns("/**");
    }
}
