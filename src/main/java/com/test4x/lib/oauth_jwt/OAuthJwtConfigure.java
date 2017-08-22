package com.test4x.lib.oauth_jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test4x.lib.oauth_jwt.biz.PrincipalRepo;
import com.test4x.lib.oauth_jwt.jwt.JwtAuthenticationProvider;
import com.test4x.lib.oauth_jwt.jwt.JwtAuthenticationTokenFilter;
import com.test4x.lib.oauth_jwt.jwt.JwtTokenUtil;
import com.test4x.lib.oauth_jwt.oauth.DefaultOAuthClient;
import com.test4x.lib.oauth_jwt.oauth.OAuthFilter;
import com.test4x.lib.oauth_jwt.oauth.OAuthProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.TimeZone;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Configuration
public class OAuthJwtConfigure extends WebSecurityConfigurerAdapter {

    @Configuration
    public static class DefaultBean {
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
        public JwtTokenUtil jwtTokenUtil(ObjectMapper objectMapper,
                                         @Value("${test4x.jwt.secret:XuGuOfAn}") String secret,
                                         @Value("${test4x.jwt.expiration:168}") Long expiration) {
            return new JwtTokenUtil(objectMapper, secret, expiration);
        }

        @Bean
        @ConditionalOnMissingBean(DefaultOAuthClient.class)
        public DefaultOAuthClient oAuthClient(OAuthProperties oAuthProperties,
                                              ObjectMapper objectMapper) {
            System.out.println("创建默认DefaultOAuthClient");
            return new DefaultOAuthClient(oAuthProperties, objectMapper);
        }

    }


    @Autowired
    private PrincipalRepo principalRepo;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private DefaultOAuthClient oAuthClient;

    @Value("${test4x.jwt.tokenName:X-Auth-Token}")
    private String tokenName;

    @Value("${test4x.loginPath:/sso}")
    private String loginPath;


    @Bean
    @ConditionalOnWebApplication
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping(loginPath);
            }
        };
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider();
    }

    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter(tokenName, principalRepo, jwtTokenUtil);
    }

    public OAuthFilter oAuthFilter() throws Exception {
        OAuthFilter oAuthFilter = new OAuthFilter(loginPath);
        oAuthFilter.setApplicationEventPublisher(getApplicationContext());
        oAuthFilter.setAuthClient(oAuthClient);
        oAuthFilter.setJwtTokenUtil(jwtTokenUtil);
        oAuthFilter.setAuthenticationManager(authentication -> null); //实际上用不上
        return oAuthFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.setSharedObject(RequestCache.class, new NullRequestCache());//这儿没有找到太好的设置方法，hack一下

        http
                .csrf().disable()//关闭csrf
                .sessionManagement().disable()//不使用session
                .requestCache().disable()//不使用缓存
                .securityContext().disable()//关了再说
                .formLogin().disable()//关闭乱七八糟的登录
                .httpBasic().disable()//关闭basic登录
                .logout().disable()//关闭登出
                .cors();//当然是关闭cors

        http.authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()//允许跨域
                .anyRequest().authenticated();

        http.exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(SC_UNAUTHORIZED, authException.getMessage());
                });


        http.addFilterBefore(oAuthFilter(), RequestCacheAwareFilter.class);
        http.addFilterBefore(authenticationTokenFilterBean(), RequestCacheAwareFilter.class);

        http.headers().cacheControl();
    }
}
