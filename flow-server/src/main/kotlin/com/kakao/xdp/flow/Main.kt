package com.kakao.xdp.flow

import com.kakao.xdp.commons.logging.handleSLF4JBridge
import com.kakao.xdp.commons.server.Bootstrap
import com.kakao.xdp.flow.config.ServerConfiguration
import com.kakao.xdp.flow.config.WebConfiguration

object Main : Bootstrap() {
    @JvmStatic
    fun main(args: Array<String>) {
        handleSLF4JBridge()

        val applicationContext = initApplicationContext(ServerConfiguration::class.java)

        shutdownHook(applicationContext)
    }
}