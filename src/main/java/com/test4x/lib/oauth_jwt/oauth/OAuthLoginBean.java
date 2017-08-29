package com.test4x.lib.oauth_jwt.oauth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 这只是一个搬运工
 * 包含accesstoken和userInfo
 */
public class OAuthLoginBean<A, R> implements Authentication {
    private final static UnsupportedOperationException E = new UnsupportedOperationException("Use OAuthAuthenticationToken is wrong");

    public A accessToken;
    public R userInfo;


    public OAuthLoginBean(A accessToken, R userInfo) {
        this.accessToken = accessToken;
        this.userInfo = userInfo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        throw E;
    }

    @Override
    public Object getCredentials() {
        throw E;
    }

    @Override
    public Object getDetails() {
        throw E;
    }

    @Override
    public Object getPrincipal() {
        throw E;
    }

    @Override
    public boolean isAuthenticated() {
        throw E;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw E;
    }

    @Override
    public String getName() {
        throw E;
    }
}
