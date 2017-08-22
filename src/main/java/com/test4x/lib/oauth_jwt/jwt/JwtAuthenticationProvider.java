package com.test4x.lib.oauth_jwt.jwt;

import com.test4x.lib.oauth_jwt.biz.AuthUser;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;


/**
 * 对JwtAuthentication进行检查
 * 不用再从数据库里查找数据了
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(JwtAuthentication.class, authentication,
                "Only JwtAuthentication is supported");
        JwtAuthentication jwtAuthentication = (JwtAuthentication) authentication;
        //这儿只要检查下authUser是否正常就好了
        AuthUser userInfo = jwtAuthentication.getUser();
        if (!userInfo.isAccountNonLocked()) {
            return authentication;
        }
        throw new LockedException("Account has been blocked");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthentication.class
                .isAssignableFrom(authentication));
    }
}
