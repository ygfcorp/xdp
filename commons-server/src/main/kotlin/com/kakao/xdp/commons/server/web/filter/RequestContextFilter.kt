package com.kakao.xdp.commons.server.web.filter

import com.kakao.xdp.commons.context.RequestContext
import org.slf4j.MDC
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

const val REQUEST_ID = "requestId"

class RequestContextFilter : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest,
                                  response: HttpServletResponse,
                                  filterChain: FilterChain) {
        val requestContext: RequestContext = extractOrCreate(request)
        MDC.put(REQUEST_ID, requestContext.requestId)
        try {
            filterChain.doFilter(request, response)
        } finally {
            MDC.remove(REQUEST_ID)
        }
    }

    private fun extractOrCreate(request: HttpServletRequest) = RequestContext()
}
