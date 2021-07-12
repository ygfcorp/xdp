package com.kakao.xdp.commons.server.infra.undertow

import com.kakao.xdp.commons.server.infra.health.HealthSwitcher
import io.undertow.server.HandlerWrapper
import io.undertow.server.HttpHandler

class GracefulShutdownHandlerWrapper(private val healthSwitcher: HealthSwitcher) : HandlerWrapper {
    override fun wrap(handler: HttpHandler) = GracefulShutdownHandler(healthSwitcher, handler)
}