package com.kakao.xdp.commons.server.exception

import com.kakao.xdp.commons.exception.ServiceException

class ForbiddenException(message: String, cause: Throwable?) : ServiceException(message, cause) {
    constructor(message: String) : this(message, null)
}
