package com.heima.wemedia;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 应用程序的主入口类
 */
@SpringBootApplication // 标记这是一个 Spring Boot 应用程序
@EnableDiscoveryClient // 启用服务发现客户端，以便注册到注册中心（如 Nacos、Eureka 等）
@MapperScan("com.heima.wemedia.mapper") // 扫描 mapper 接口所在的包，自动生成其实现
@EnableFeignClients(basePackages = "com.heima.apis") // 启用 Feign 客户端，并指定 Feign 接口所在的包路径
@EnableAsync
@EnableScheduling
public class WemediaApplication {

    /**
     * 应用程序的主方法，用于启动 Spring Boot 应用
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(WemediaApplication.class, args); // 运行 Spring Boot 应用
    }

    /**
     * 配置 MyBatis Plus 拦截器，用于分页等功能
     * @return 配置了分页拦截器的 MybatisPlusInterceptor 对象
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor(); // 创建 MyBatis Plus 拦截器实例
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL)); // 添加分页拦截器，指定数据库类型为 MySQL
        return interceptor; // 返回拦截器对象
    }
}
