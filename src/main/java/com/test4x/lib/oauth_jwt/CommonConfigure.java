package com.test4x.lib.oauth_jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test4x.lib.oauth_jwt.biz.PrincipalRepo;
import com.test4x.lib.oauth_jwt.jwt.JwtAuthenticationProvider;
import com.test4x.lib.oauth_jwt.jwt.JwtTokenUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.TimeZone;

@Configuration
public class CommonConfigure {

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        System.out.println("创建默认ObjectMapper");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return objectMapper;
    }

    @Bean
    @ConditionalOnMissingBean(PrincipalRepo.class)
    public PrincipalRepo principalRepo() {
        System.out.println("创建默认principalRepo");
        return id -> {
            throw new UnsupportedOperationException("需要实现PrincipalRepo");
        };
    }

    @Bean
    @ConditionalOnMissingBean(JwtTokenUtil.class)
    public JwtTokenUtil jwtTokenUtil(OauthJwtProp oauthJwtProp,
                                     ObjectMapper objectMapper) {
        return new JwtTokenUtil(objectMapper,
                oauthJwtProp.getJwt().getSecret(),
                oauthJwtProp.getJwt().getExpiration());
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider();
    }

    @Bean
    @ConditionalOnWebApplication
    public WebMvcConfigurer corsConfigurer(OauthJwtProp oauthJwtProp) {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping(oauthJwtProp.getLoginPath());
            }
        };
    }
}
