package com.test4x.lib.oauth_jwt.jwt

import com.test4x.lib.oauth_jwt.biz.PrincipalRepo
import com.test4x.lib.oauth_jwt.util.valid
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * jwt鉴权filter
 */
class JwtAuthenticationTokenFilter
constructor(private val tokenName: String,
            private val principalRepo: PrincipalRepo,
            private val jwtTokenUtil: JwtTokenUtil) : OncePerRequestFilter() {

    private val webAuthenticationDetailsSource = WebAuthenticationDetailsSource()


    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val authToken = request.getHeader(this.tokenName)
        // authToken.startsWith("Bearer ")
        // String authToken = header.substring(7);
        if (authToken != null && SecurityContextHolder.getContext().authentication == null) {
            val claimsJws: Jws<Claims>
            try {
                claimsJws = jwtTokenUtil.parseToken(authToken)
            } catch (e: Exception) { //解析出错
                chain.doFilter(request, response)
                return
            }
            val user = principalRepo.getUserById(claimsJws.body.subject)
            if (claimsJws.valid(user)) {
                val authentication = JwtAuthentication(user)
                authentication.details = webAuthenticationDetailsSource.buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            } else { //验证不通过
                //todo 错误处理
            }
        }
        chain.doFilter(request, response)
    }
}