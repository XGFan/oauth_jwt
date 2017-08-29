package com.test4x.lib.oauth_jwt.oauth

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import com.test4x.lib.oauth_jwt.OauthJwtProp
import io.jsonwebtoken.Claims
import net.dongliu.requests.RawResponse
import net.dongliu.requests.Requests
import org.springframework.security.authentication.AuthenticationServiceException
import java.io.IOException
import java.util.*

open class DefaultOAuthService
constructor(var prop: OauthJwtProp.OAuthProp, val objectMapper: ObjectMapper = ObjectMapper()) : OAuthService<Map<String, Any>, Map<String, Any>> {


    protected open val mapType = TypeFactory.defaultInstance().constructMapType(Map::class.java, String::class.java, Any::class.java)


    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, AuthenticationServiceException::class)
    override fun acquireAccessToken(code: String): Map<String, Any> {
        val build = Requests.post(prop.tokenUri)
        val para = mapOf<String, String>(
                OAuthConstants.OAUTH_CLIENT_ID to prop.id,
                OAuthConstants.OAUTH_CLIENT_SECRET to prop.secret,
                OAuthConstants.OAUTH_CODE to code,
                OAuthConstants.OAUTH_REDIRECT_URI to prop.redirectUri,
                OAuthConstants.OAUTH_GRANT_TYPE to prop.grantType
        )
        build.headers(mapOf("Accept" to "application/json"))
        when (prop.authenticationScheme) {
            OAuthConstants.ParameterStyle.BODY -> {
                build.body(para)
            }
            OAuthConstants.ParameterStyle.HEADER -> {
                throw  UnsupportedOperationException("不支持HEADER")
            }
            OAuthConstants.ParameterStyle.QUERY -> {
                build.params(para)
            }
        }
        val rawResponse: RawResponse = build.send()
        val json = rawResponse.readToText()
        if (rawResponse.statusCode != 200) {
            throw AuthenticationServiceException(json)
        }
        return strToMap(json)
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(AuthenticationServiceException::class)
    override fun acquireUserInfo(accessToken: Map<String, Any>): Map<String, Any> {
        val map = HashMap<String, String>()
        map.put("access_token", accessToken.get("access_token").toString())
        val rawResponse = Requests.get(prop.userInfoUri).params(map).send()
        val json = rawResponse.readToText()
        if (rawResponse.statusCode != 200) {
            throw AuthenticationServiceException(json)
        }
        return strToMap(json)
    }

    open fun strToMap(json: String): Map<String, Any> {
        try {
            return objectMapper.readValue<Map<String, Any>>(json, mapType)
        } catch (e: IOException) {
            throw AuthenticationServiceException(json, e)
        }
    }


    override fun handleAndAuth(accessToken: Map<String, Any>, userInfo: Map<String, Any>): Claims {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
