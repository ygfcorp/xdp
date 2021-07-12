package com.kakao.xdp.commons.exception

abstract class ServiceException(message: String, cause: Throwable?) : RuntimeException(message, cause) {
    constructor(message: String) : this(message, null)
}
