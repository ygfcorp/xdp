package com.kakao.xdp.commons.server.web.servlet

import com.kakao.xdp.commons.jackson.Jackson
import com.kakao.xdp.commons.server.exception.ServerExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.util.WebUtils
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class XDPDispatcherServlet(servletAppContext: WebApplicationContext) : DispatcherServlet(servletAppContext) {
    private val NOT_FOUND_CODE = HttpStatus.NOT_FOUND.value()
    private var notFoundBody: ByteArray = Jackson.writeValueAsBytes(ServerExceptionHandler.ErrorMessage(NOT_FOUND_CODE, "Invalid URL mapping."))

    @Throws(Exception::class)
    override fun noHandlerFound(request: HttpServletRequest, response: HttpServletResponse) {
        if (logger.isInfoEnabled) {
            logger.info("No mapping for " + request.method + " " + getRequestUri(request))
        }
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.setContentLength(notFoundBody.size)
        response.status = NOT_FOUND_CODE

        try {
            response.outputStream.use { out -> out.write(notFoundBody) }
        } catch (e: IOException) {
            logger.warn("Fail to write error message", e)
        }
    }

    private fun getRequestUri(request: HttpServletRequest): String? {
        var uri = request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE)?.let { it as String }
        if (uri == null) {
            uri = request.requestURI
        }
        return uri
    }
}
