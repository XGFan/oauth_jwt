package com.test4x.lib.oauth_jwt.oauth;

import com.test4x.lib.oauth_jwt.jwt.JwtTokenUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.function.Function;

public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private JwtTokenUtil jwtTokenUtil;
    /**
     * 用来抽出有用的用户信息，避免生成的token过大
     */
    private Function<Map<String, Object>, Map<String, Object>> userInfoTrans = Function.identity();

    public OAuthSuccessHandler(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            OAuthLoginBean au = (OAuthLoginBean) authentication;
            Map<String, Object> tinyUser = userInfoTrans.apply(au.userInfo);
            String token = this.jwtTokenUtil.doGenerateToken(tinyUser.get("id").toString(), tinyUser);
            PrintWriter writer = response.getWriter();
            writer.append(token);
            writer.close();
        } catch (Exception ignore) {
            response.sendError(401, "Authentication Broken");
        }
    }
}
