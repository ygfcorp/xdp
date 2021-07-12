package com.kakao.xdp.commons.server.infra

import org.apache.commons.lang3.StringUtils
import org.springframework.web.servlet.function.ServerRequest

const val X_FORWARDED_FOR_HEADER = "x-forwarded-for"

fun actualClientIP(request: ServerRequest): String {
    val xForwardedFor: String = request.headers().firstHeader(X_FORWARDED_FOR_HEADER).orEmpty()

    return if (StringUtils.isBlank(xForwardedFor)) {
        request.remoteAddress().toString()
    } else {
        xForwardedFor.split(",").toTypedArray()[0]
    }
}
