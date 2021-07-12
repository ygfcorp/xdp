package com.kakao.xdp.metrics.collector.statsd

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit

class StatsDMetricCollectorTest {
    private val cut = StatsDMetricCollector("test", "test", "malibu-dev-mysql.ay1.krane.9rum.cc", 8125, true)

    @Test
    fun sendUDP() {
        val counter = cut.counter("sampler")
        counter.inc()
        counter.inc()
        counter.inc()
        counter.inc()

        TimeUnit.SECONDS.sleep(10)
    }

    @Disabled
    @Test
    fun longRunning() {
        val c = cut.counter("monotonic_total")

        val start = System.currentTimeMillis()
        val fiveM = TimeUnit.MINUTES.toMillis(5)

        val r = ThreadLocalRandom.current()

        while (true) {
            if (System.currentTimeMillis() > start + fiveM) {
                break
            }

            TimeUnit.SECONDS.sleep(r.nextLong(3))

            c.inc(r.nextLong(100))
        }

        TimeUnit.SECONDS.sleep(10)

        println(c.count)
    }
}
