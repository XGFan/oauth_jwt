package com.test4x.lib.oauth_jwt.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.test4x.lib.oauth_jwt.biz.AuthUser
import com.test4x.lib.oauth_jwt.util.iatBefore
import com.test4x.lib.oauth_jwt.util.isExpired
import com.test4x.lib.oauth_jwt.util.isSubject
import com.test4x.lib.oauth_jwt.util.toDate
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

class JwtTokenUtil
constructor(val objectMapper: ObjectMapper,
            val secret: String,
            val expiration: Long) : Serializable {


    fun getSubjectFromToken(token: String): String {
        return getClaimFromToken(token, { it.subject })
    }

    fun getIssuedAtDateFromToken(token: String): Date {
        return getClaimFromToken(token, { it.issuedAt })
    }

    fun getExpirationDateFromToken(token: String): Date {
        return getClaimFromToken(token, { it.expiration })
    }


    fun <T> getClaimFromToken(token: String, claimsResolver: (Claims) -> T): T {
        val claims = getAllClaimsFromToken(token)
        return claimsResolver.invoke(claims)
    }

    private fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .body
    }

    private fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(Date(System.currentTimeMillis()))
    }

    private fun isCreatedBeforeLastPasswordReset(created: Date, lastPasswordReset: Date?): Boolean {
        return lastPasswordReset != null && created.before(lastPasswordReset)
    }

    fun doGenerateToken(claims: Claims): String {
        //创建时间与过期时间
        val now = LocalDateTime.now()
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now.toDate())
                .setExpiration(now.plusHours(expiration).toDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact()
    }


    fun doGenerateToken(claims: Map<String, Any>): String {
        //创建时间与过期时间
        val now = LocalDateTime.now()
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now.toDate())
                .setExpiration(now.plusHours(expiration).toDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact()
    }

    @Suppress("UNCHECKED_CAST")
    fun doGenerateToken(subject: String, claims: Any): String {
        //创建时间与过期时间
        val now = LocalDateTime.now()
        return Jwts.builder()
                .setClaims(objectMapper.convertValue(claims, Map::class.java) as Map<String, Any>?)
                .setSubject(subject)
                .setIssuedAt(now.toDate())
                .setExpiration(now.plusHours(expiration).toDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact()
    }


    fun canTokenBeRefreshed(token: String, lastPasswordReset: Date): Boolean? {
        val created = getIssuedAtDateFromToken(token)
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset) && !isTokenExpired(token)
    }

    fun refreshToken(token: String): String {
        val claims = getAllClaimsFromToken(token)
        claims.issuedAt = Date(System.currentTimeMillis())
        return doRefreshToken(claims)
    }

    fun doRefreshToken(claims: Claims): String {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact()
    }

    fun validateToken(token: String, user: AuthUser): Boolean {
        val allClaimsFromToken = parseToken(token)
        return allClaimsFromToken.isSubject(user.id) //id相符
                && !allClaimsFromToken.isExpired //未过期
                && !allClaimsFromToken.iatBefore(user.lastPwdSetDate) //iat时间不早于修改密码时间
    }

    /**
     * 解析token
     */
    fun parseToken(token: String): Jws<Claims> {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
    }

}