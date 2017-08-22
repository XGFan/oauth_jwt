package com.test4x.lib.oauth_jwt.biz;


/**
 * 获取用户信息
 */
public interface PrincipalRepo {
    /**
     * 根据唯一id从系统内获取用户信息
     */
    AuthUser getUserById(String id);
}
