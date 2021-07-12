package com.kakao.xdp.flow.domain.nifi

import com.kakao.xdp.commons.exception.ServiceException

class NifiException(message: String, cause: Throwable?) : ServiceException(message, cause) {
    constructor(message: String) : this(message, null)
}