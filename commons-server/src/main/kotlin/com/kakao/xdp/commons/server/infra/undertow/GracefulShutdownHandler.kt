package com.kakao.xdp.commons.server.infra.undertow

import com.kakao.xdp.commons.logging.logger
import com.kakao.xdp.commons.server.infra.health.HealthSwitcher
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import org.slf4j.Logger

class GracefulShutdownHandler(private val healthSwitcher: HealthSwitcher, private val next: HttpHandler) : HttpHandler {
    private val logger: Logger = logger(GracefulShutdownHandler::class.java)

    @Throws(Exception::class)
    override fun handleRequest(exchange: HttpServerExchange) {
        if (!healthSwitcher.alive()) {
            exchange.addResponseCommitListener { beforeCommitExchange: HttpServerExchange ->
                logger.info("healthSwitcher is DEAD. Close connection.")
                beforeCommitExchange.isPersistent = false
            }
        }
        next.handleRequest(exchange)
    }
}
