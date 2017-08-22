package com.test4x.lib.oauth_jwt.util

import com.test4x.lib.oauth_jwt.biz.AuthUser
import io.jsonwebtoken.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

fun LocalDateTime.toDate(): Date {
    return Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
}

fun Date.toLDT(): LocalDateTime {
    return LocalDateTime.ofInstant(this.toInstant(), ZoneId.systemDefault())
}

fun Jws<Claims>.refresh(hours: Long, secret: String): String {
    if (this.body.expiration.toLDT().isBefore(LocalDateTime.now().plusDays(1L))) {
        val now = LocalDateTime.now()
        this.body.issuedAt = now.toDate()
        this.body.expiration = now.plusHours(hours).toDate()
        return Jwts.builder()
                .setClaims(this.body)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact()
    } else {
        throw UnsupportedJwtException("token has been expired over 1 day")
    }
}

val Jws<Claims>.isExpired: Boolean
    get() = this.body.expiration.toLDT().isBefore(LocalDateTime.now())


fun Jws<Claims>.iatBefore(ldt: LocalDateTime?): Boolean {
    return this.body.issuedAt.before(ldt?.toDate() ?: return false)
}

fun Jws<Claims>.isSubject(id: String): Boolean {
    return this.body.subject == id
}

fun Jws<Claims>.valid(user: AuthUser?): Boolean {
    user ?: return false
    return this.isSubject(user.id) //id相符
            && !this.isExpired //未过期
            && !this.iatBefore(user.lastPwdSetDate) //iat时间不早于修改密码时间
}