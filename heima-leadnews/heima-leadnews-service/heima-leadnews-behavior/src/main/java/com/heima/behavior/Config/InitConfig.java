package com.heima.behavior.Config;

import com.heima.behavior.gateway.filter.AuthorizationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InitConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {// 注册自定义拦截器 WmTokeninterceptor，并指定拦截的路径模式

        registry.addInterceptor(new AuthorizationInterceptor())
                .addPathPatterns("/**");
    }


}