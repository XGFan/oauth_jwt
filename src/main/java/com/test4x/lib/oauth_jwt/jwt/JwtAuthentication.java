package com.test4x.lib.oauth_jwt.jwt;

import com.test4x.lib.oauth_jwt.biz.AuthUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * Jwt认证信息
 */
public class JwtAuthentication extends AbstractAuthenticationToken {

    private AuthUser userInfo;

    public AuthUser getUser() {
        return userInfo;
    }

    public JwtAuthentication(AuthUser userInfo) {
        super(userInfo.getAuthorities());
        this.userInfo = userInfo;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.userInfo;
    }
}
