package com.test4x.lib.oauth_jwt

import com.test4x.lib.oauth_jwt.oauth.OAuthConstants
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "oauth-jwt")
class OauthJwtProp {
    var loginPath = "/login"

    var oauth: OAuthProp = OAuthProp()

    var jwt: JwtProp = JwtProp()


}

class OAuthProp {
    lateinit var id: String
    lateinit var secret: String
    lateinit var tokenUri: String
    lateinit var userInfoUri: String

    var grantType: String = "authorization_code"
    var authenticationScheme: OAuthConstants.ParameterStyle = OAuthConstants.ParameterStyle.BODY
}

class JwtProp {
    var tokenName = "X-Auth-Token"
    var secret = "hello@world"
    var expiration: Long = 168
}