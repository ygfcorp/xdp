package com.kakao.xdp.metrics.collector.statsd

import com.codahale.metrics.Counter
import com.codahale.metrics.Gauge
import com.codahale.metrics.Histogram
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.MetricRegistry.MetricSupplier
import com.codahale.metrics.Timer
import com.kakao.xdp.commons.logging.logger
import com.kakao.xdp.commons.system.hostname
import com.kakao.xdp.metrics.collector.MetricCollector
import com.readytalk.metrics.StatsDReporter
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class StatsDMetricCollector(component: String, phase: String, host: String, port: Int, startOnCreation: Boolean) :
    MetricCollector, AutoCloseable {
    private val logger: Logger = logger(StatsDMetricCollector::class.java)

    private val successFailCounterCache = ConcurrentHashMap<String, SuccessFailCounter>()
    private val successFailTimerCache = ConcurrentHashMap<String, SuccessFailTimer>()

    private val registry: MetricRegistry = ConfigurableMetricRegistry()
    private val reporter: StatsDReporter? = if (StringUtils.isNotBlank(host) && port > 0) {
        StatsDReporter.forRegistry(registry())
            .withTag("component=$component")
            .withTag("phase=$phase")
            .withTag("host=$hostname")
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build(host, port)
            .also {
                if (startOnCreation) {
                    logger.info("Metric will be reported at every 10 secs.");
                    it.start(10, TimeUnit.SECONDS);
                } else {
                    logger.info("Metric will not be reported automatically. You should report manually.");
                }
            }
    } else {
        logger.info("No metric host specified. Skip reporting metric.");
        null
    }

    fun registry(): MetricRegistry {
        return registry
    }

    fun gauge(name: String, supplier: MetricSupplier<Gauge<Any>>) {
        registry().gauge(name, supplier)
    }

    fun counter(name: String): Counter {
        return registry().counter(name)
    }

    fun histogram(name: String): Histogram {
        return registry().histogram(name)
    }

    fun timer(name: String): Timer {
        return registry().timer(name)
    }

    fun successFailTimer(name: String) = successFailTimerCache.computeIfAbsent(name) { SuccessFailTimer(timer("$name,r=s"), timer("$name,r=f")) }

    fun successFailCounter(name: String) = successFailCounterCache.computeIfAbsent(name) { SuccessFailCounter(counter("$name,r=s"), counter("$name,r=f")) }

    fun report() {
        reporter?.report()
    }

    override fun close() {
        report()
        reporter?.close()
    }
}
