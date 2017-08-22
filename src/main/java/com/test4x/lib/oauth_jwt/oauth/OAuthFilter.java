package com.test4x.lib.oauth_jwt.oauth;

import com.test4x.lib.oauth_jwt.jwt.JwtTokenUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class OAuthFilter extends AbstractAuthenticationProcessingFilter {

    private DefaultOAuthClient authClient;


    public void setAuthClient(DefaultOAuthClient authClient) {
        this.authClient = authClient;
    }

    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        setAuthenticationSuccessHandler(new OAuthSuccessHandler(jwtTokenUtil));
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
        String code = getCodeFromRequest(request);
        if (code == null) {
            //应该返回401，前往授权
            response.sendError(401, "Redirect to authorization uri");
        }
        Map<String, Object> accessToken;
        try {
            accessToken = authClient.acquireAccessToken(code);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(401, "Can not get access token");
            return null;
        }
        Map<String, Object> userInfo = authClient.acquireUserInfo(accessToken.get("access_token").toString());
        //如果用户信息也完整
        //就使用accessToken和userInfo生成token
        return new OAuthLoginBean(accessToken, userInfo);
    }


    private String getCodeFromRequest(HttpServletRequest request) {
        return request.getParameter("code");
    }

}
