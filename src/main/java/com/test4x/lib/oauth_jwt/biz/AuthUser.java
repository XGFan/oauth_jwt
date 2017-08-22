package com.test4x.lib.oauth_jwt.biz;

import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * 授权用户
 */
public interface AuthUser {
    /**
     * 唯一id
     * 用来辨明身份
     */
    String getId();

    /**
     * 上次修改密码时间
     */
    LocalDateTime getLastPwdSetDate();

    /**
     * 权限
     */
    Collection<GrantedAuthority> getAuthorities();

    /**
     * 账户是否被锁定
     */
    boolean isAccountNonLocked();
}
