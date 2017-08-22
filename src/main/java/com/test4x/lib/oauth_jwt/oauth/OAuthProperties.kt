package com.test4x.lib.oauth_jwt.oauth

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "test4x.oauth")
class OAuthProperties {
    lateinit var id: String
    lateinit var secret: String
    lateinit var tokenUri: String
    lateinit var redirectUri: String
    lateinit var grantType: String
    lateinit var authenticationScheme: OAuthConstants.ParameterStyle
    lateinit var userInfoUri: String


    fun v(){

    }
}
