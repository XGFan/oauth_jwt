package com.test4x.lib.oauth_jwt.oauth

import io.jsonwebtoken.Claims
import org.springframework.security.authentication.AuthenticationServiceException

interface OAuthService<A, R> {
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, AuthenticationServiceException::class)
    fun acquireAccessToken(code: String): A

    @Suppress("UNCHECKED_CAST")
    @Throws(AuthenticationServiceException::class)
    fun acquireUserInfo(accessToken: A): R


    /**
     * 这里就不使用event来通知了，直接在函数里面
     * 进行保存用户数据以及生成claims操作
     */
    fun handleAndAuth(accessToken: A, userInfo: R): Claims
}