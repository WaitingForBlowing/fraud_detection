package cn.moozlee.fraudetection.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    private CorsConfiguration buildConfig(){
        CorsConfiguration configuration = new CorsConfiguration();
        //设置属性
        //允许跨域的域名,SpringBoot2.4.0版本后，需要将addAllowedOrigin改成addAllowedOriginPattern
        configuration.addAllowedOriginPattern("*");
        //允许跨域的请求头
        configuration.addAllowedHeader("*");
        //允许跨域的请求方法
        configuration.addAllowedMethod("*");
        //表示跨域请求的时候是否使用的是同一个session
        configuration.setAllowCredentials(true);

        return configuration;
    }

    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }
}
