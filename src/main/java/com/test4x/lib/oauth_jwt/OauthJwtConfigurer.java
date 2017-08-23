package com.test4x.lib.oauth_jwt;

import com.test4x.lib.oauth_jwt.biz.PrincipalRepo;
import com.test4x.lib.oauth_jwt.jwt.JwtAuthenticationTokenFilter;
import com.test4x.lib.oauth_jwt.jwt.JwtTokenUtil;
import com.test4x.lib.oauth_jwt.oauth.DefaultOAuthClient;
import com.test4x.lib.oauth_jwt.oauth.OAuthFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public class OauthJwtConfigurer {
    private ApplicationContext applicationContext;

    OauthJwtConfigurer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void configure(HttpSecurity http) throws Exception {
        OauthJwtProp oauthJwtProp = this.applicationContext.getBean(OauthJwtProp.class);


        DefaultOAuthClient oAuthClient = this.applicationContext.getBean(DefaultOAuthClient.class);
        JwtTokenUtil jwtTokenUtil = this.applicationContext.getBean(JwtTokenUtil.class);
        PrincipalRepo principalRepo = this.applicationContext.getBean(PrincipalRepo.class);

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
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll();//允许跨域

        http.exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(SC_UNAUTHORIZED, authException.getMessage());
                });


        http.addFilterBefore(oAuthFilter(oauthJwtProp.getLoginPath(), oAuthClient, jwtTokenUtil), RequestCacheAwareFilter.class);
        http.addFilterBefore(authenticationTokenFilterBean(oauthJwtProp.getJwt().getTokenName(), principalRepo, jwtTokenUtil), RequestCacheAwareFilter.class);

        http.headers().cacheControl();
    }


    private JwtAuthenticationTokenFilter authenticationTokenFilterBean(String tokenName, PrincipalRepo principalRepo, JwtTokenUtil jwtTokenUtil) throws Exception {
        return new JwtAuthenticationTokenFilter(tokenName, principalRepo, jwtTokenUtil);
    }

    private OAuthFilter oAuthFilter(String loginPath, DefaultOAuthClient oAuthClient, JwtTokenUtil jwtTokenUtil) throws Exception {
        OAuthFilter oAuthFilter = new OAuthFilter(loginPath);
        oAuthFilter.setApplicationEventPublisher(this.applicationContext);
        oAuthFilter.setAuthClient(oAuthClient);
        oAuthFilter.setJwtTokenUtil(jwtTokenUtil);
        oAuthFilter.setAuthenticationManager(authentication -> null); //实际上用不上
        return oAuthFilter;
    }


}
