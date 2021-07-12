package com.kakao.xdp.commons.server.web.filter

import com.kakao.xdp.commons.server.exception.ServerExceptionHandler
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ExceptionHandlerFilter(private val serverExceptionHandler: ServerExceptionHandler) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (t: Throwable) {
            serverExceptionHandler.handle(request, response, t)
        }
    }
}