package com.kakao.xdp.commons.server.exception

import com.kakao.xdp.commons.exception.ServiceException

class NotFoundException(msg: String): ServiceException(msg)
