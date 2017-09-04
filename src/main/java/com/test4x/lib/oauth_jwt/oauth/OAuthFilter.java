package com.test4x.lib.oauth_jwt.oauth;

import com.test4x.lib.oauth_jwt.jwt.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.Function;

public class OAuthFilter<A, R> extends AbstractAuthenticationProcessingFilter {

    private OAuthService<A, R> authService;

    private JwtTokenUtil jwtTokenUtil;

    private Function<HttpServletRequest, String> tokenGetter = request -> request.getParameter("code");

    private Function<HttpServletRequest, String> redirectUriGetter = request -> request.getParameter("redirectUri");

    public void setAuthService(OAuthService authService) {
        this.authService = authService;
    }

    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public void setTokenGetter(Function<HttpServletRequest, String> tokenGetter) {
        this.tokenGetter = tokenGetter;
    }

    public void setRedirectUriGetter(Function<HttpServletRequest, String> redirectUriGetter) {
        this.redirectUriGetter = redirectUriGetter;
    }

    public OAuthFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
        //不允许创建session
        super.setAllowSessionCreation(false);
        //授权成功，就直接滚吧
        super.setContinueChainBeforeSuccessfulAuthentication(false);
        super.setAuthenticationFailureHandler((request, response, exception) -> {
            response.sendError(401, "Authentication Failure");
        });
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        String code = tokenGetter.apply(request);
        String redirectUri = redirectUriGetter.apply(request);
        if (code == null || redirectUri == null) {
            //应该返回401，前往授权
            throw new BadCredentialsException("code or redirectUri is null");
        }
        A accessToken;
        try {
            accessToken = authService.acquireAccessToken(code, redirectUri);
        } catch (Exception e) {
            throw new BadCredentialsException("Can not get access token", e);
        }
        R userInfo = authService.acquireUserInfo(accessToken);
        //如果用户信息也完整
        //就使用accessToken和userInfo生成token
        return new OAuthLoginBean<A, R>(accessToken, userInfo);
    }


    /**
     * 成功之后该干嘛干嘛
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        OAuthLoginBean<A, R> auth = (OAuthLoginBean<A, R>) authResult;
        SecurityContextHolder.getContext().setAuthentication(authResult);
        Claims claims = authService.handleAndAuth(auth.accessToken, auth.userInfo);
        String token = jwtTokenUtil.doGenerateToken(claims);
        //todo 这儿应该写成json更合适一点
        PrintWriter writer = response.getWriter();
        writer.append(token);
        writer.close();
    }

}
