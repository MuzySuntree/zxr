package com.youngman.hostel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置。
 *
 * <p>前端可能部署在不同端口或服务器地址（例如 http://139.155.173.48:30000），
 * 后端接口部署在另一个端口（例如 http://139.155.173.48:30001）。当前项目未使用 Cookie
 * 凭证跨域，因此允许任意来源访问 API，避免预检请求因 Origin 不在白名单中而失败。</p>
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
