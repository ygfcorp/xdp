package com.kakao.xdp.commons.server

import com.kakao.xdp.commons.logging.logger
import com.kakao.xdp.commons.server.infra.health.HealthSwitcher
import org.slf4j.Logger
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import java.util.concurrent.CountDownLatch

abstract class Bootstrap {
    private val logger: Logger = logger(javaClass)

    fun <T> initApplicationContext(clazz: Class<T>): AnnotationConfigWebApplicationContext {
        ensurePhase()
        return AnnotationConfigWebApplicationContext().apply {
            register(clazz)
            refresh()
            start()
        }
    }

    private fun ensurePhase() {
        val phase = System.getenv("phase") ?: throw IllegalArgumentException("`phase` should be specified.");
        logger.info("Set phase: {}", phase)
    }

    fun shutdownHook(applicationContext: AnnotationConfigWebApplicationContext) {
        val latch = CountDownLatch(1)

        Runtime.getRuntime().addShutdownHook(Thread {
            logger.info("Got shutdown signal. Try to close.")

            applicationContext.stop()
            applicationContext.close()
            latch.countDown()
        })

        applicationContext.getBean(HealthSwitcher::class.java).goingUp()

        latch.await()

        logger.info("Server shutdown.")
    }
}
