package com.kakao.xdp.commons.server.exception

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.kakao.xdp.commons.exception.ServiceException
import com.kakao.xdp.commons.jackson.Jackson
import com.kakao.xdp.commons.logging.logger
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException
import org.springframework.web.servlet.ModelAndView
import java.io.IOException
import java.security.InvalidParameterException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ServerExceptionHandler {
    private val logger = logger(ServerExceptionHandler::class.java)

    fun handle(request: HttpServletRequest, response: HttpServletResponse, t: Throwable) {
        if (response.isCommitted) {
            return
        }

        var status = HttpStatus.INTERNAL_SERVER_ERROR
        var code = status.value()
        var message = t.message
        var logLevel = LogLevel.ERROR

        when (t) {
            is ServerWebInputException -> {
                status = (t as ResponseStatusException).status
                message = "Invalid Parameter(s)."
                code = status.value()
                logLevel = LogLevel.INFO
            }
            is ResponseStatusException -> {
                status = t.status
                message = "Could not handle request."
                code = status.value()
                logLevel = LogLevel.DEBUG
            }
            is AuthorizationException -> {
                status = HttpStatus.UNAUTHORIZED
                message = t.message
                code = HttpStatus.UNAUTHORIZED.value()
                logLevel = LogLevel.INFO
            }
            is ForbiddenException -> {
                status = HttpStatus.FORBIDDEN
                message = t.message
                code = HttpStatus.FORBIDDEN.value()
                logLevel = LogLevel.INFO
            }
            is InvalidParameterException -> {
                status = HttpStatus.BAD_REQUEST
                message = t.message
                code = HttpStatus.BAD_REQUEST.value()
                logLevel = LogLevel.WARN
            }
            is ServiceException -> {
                status = HttpStatus.INTERNAL_SERVER_ERROR
                message = t.message
                code = HttpStatus.INTERNAL_SERVER_ERROR.value()
                logLevel = LogLevel.WARN
            }
        }

        log(logLevel, request, t)

        val bytes: ByteArray = Jackson.writeValueAsBytes(ErrorMessage(code, message))
        response.contentType = MediaType.APPLICATION_JSON_UTF8_VALUE
        response.setContentLength(bytes.size)
        response.status = status.value()

        try {
            response.getOutputStream().use { out -> out.write(bytes) }
        } catch (e: IOException) {
            logger.warn("Fail to write error message", e)
        }
    }

    private fun log(logLevel: LogLevel, request: HttpServletRequest, t: Throwable) {
        val dump = dumpRequest(request)
        when (logLevel) {
            LogLevel.DEBUG -> logger.debug("Exception occurred. {}\n{}", t.message, dump)
            LogLevel.INFO -> logger.info("Exception occurred. {}\n{}", t.message, dump)
            LogLevel.WARN -> logger.warn("Exception occurred. {}\n{}", t.message, dump, t)
            LogLevel.ERROR -> logger.error("Exception occurred. {}\n{}", t.message, dump, t)
        }
    }

    private fun dumpRequest(request: HttpServletRequest): String {
        val sb =
            StringBuilder().append("Request: ").append(request.method).append(" ").append(request.requestURI)
                .append("\n")
                .append("Query: ").append(request.queryString).append("\n")
                .append("Headers\n")

        request.headerNames.asIterator().forEachRemaining { name ->
            sb.append("\t").append(name).append(": ").append(request.getHeader(name)).append("\n")
        }
        return sb.toString()
    }

    fun handleToModelAndView(request: HttpServletRequest, response: HttpServletResponse, cause: Exception): ModelAndView? {
        if (!response.isCommitted) {
            handle(request, response, cause)
        }

        return ModelAndView()
    }

    class ErrorMessage @JsonCreator constructor(
        @param:JsonProperty("code") val code: Int, @param:JsonProperty("message") val message: String?
    )

    internal enum class LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
}
