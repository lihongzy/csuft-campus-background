package com.lihong.csuftcampus.config;

import com.lihong.csuftcampus.interceptor.LoginWIthRefreshTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * SpringMVC拦截配置
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginWIthRefreshTokenInterceptor loginWIthRefreshTokenInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginWIthRefreshTokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login", "/user/register", "/user/logout");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // 允许的源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的HTTP方法
                .allowedHeaders("authorization", "content-type") // 允许的请求头
                .allowCredentials(true) // 允许发送凭证（例如Cookie）
                .maxAge(3600); // 预检请求的缓存时间（单位：秒）

    }


}
