package com.kakao.xdp.commons.server.exception

import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class DefaultHandlerExceptionResolver(private val serverExceptionHandler: ServerExceptionHandler): HandlerExceptionResolver {
    override fun resolveException(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any?,
        ex: Exception
    ) = serverExceptionHandler.handleToModelAndView(request, response, ex)
}
