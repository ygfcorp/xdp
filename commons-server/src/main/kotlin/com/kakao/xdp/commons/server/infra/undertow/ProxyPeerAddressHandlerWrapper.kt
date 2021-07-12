package com.kakao.xdp.commons.server.infra.undertow

import io.undertow.server.HandlerWrapper
import io.undertow.server.HttpHandler
import io.undertow.server.handlers.ProxyPeerAddressHandler

class ProxyPeerAddressHandlerWrapper : HandlerWrapper {
    override fun wrap(handler: HttpHandler?): HttpHandler {
        return ProxyPeerAddressHandler(handler)
    }
}