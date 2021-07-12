package com.kakao.xdp.commons.exception

class HttpCommunicationException(message: String, cause: Throwable?) : ServiceException(message, cause) {
    constructor(message: String) : this(message, null)
}