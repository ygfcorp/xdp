package com.kakao.xdp.commons.server.infra.health

import com.kakao.xdp.commons.env.Phase
import com.kakao.xdp.commons.logging.logger
import org.slf4j.Logger
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.util.concurrent.atomic.AtomicReference

class HealthSwitcher(private val phase: Phase) {
    private val logger: Logger = logger(HealthSwitcher::class.java)

    private val mode = AtomicReference(Switch.DEAD)

    fun getMode() = mode.get()

    fun goingUp() {
        if (mode.get() === Switch.ALIVE) {
            return
        }
        switchTo(Switch.ALIVE)
    }

    fun goingDown() {
        if (mode.get() === Switch.DEAD) {
            return
        }
        switchTo(Switch.DEAD)
    }

    fun healthReport() = mode.get().healthReport()

    fun switchTo(toBe: Switch): HealthControlResponse {
        val asWas = mode.get()
        mode.set(toBe)
        logger.info("Health status switched: {} -> {}", asWas, toBe)
        return HealthControlResponse(asWas, toBe)
    }

    fun alive() = mode.get() == Switch.ALIVE

    enum class Switch {
        ALIVE {
            override fun healthReport(): ResponseEntity<*> {
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(HealthStatusResponse.alive())
            }
        },
        DEAD {
            override fun healthReport(): ResponseEntity<*> {
                return ResponseEntity.notFound().build<Any>()
            }
        };

        abstract fun healthReport(): ResponseEntity<*>
    }
}

class HealthStatusResponse(val status: String) {
    val timestamp: Long = System.currentTimeMillis()

    companion object {
        fun alive(): HealthStatusResponse {
            return HealthStatusResponse("ALIVE")
        }

        fun dead(): HealthStatusResponse {
            return HealthStatusResponse("DEAD")
        }
    }
}

class HealthControlResponse(private val asWas: HealthSwitcher.Switch, private val toBe: HealthSwitcher.Switch) {
    fun getAsWas(): HealthSwitcher.Switch {
        return asWas
    }

    fun getToBe(): HealthSwitcher.Switch {
        return toBe
    }
}
