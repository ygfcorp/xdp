package com.kakao.xdp.metrics.collector.statsd

import com.codahale.metrics.Histogram
import com.codahale.metrics.Metric
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.SlidingTimeWindowArrayReservoir
import com.codahale.metrics.Timer
import java.util.concurrent.TimeUnit

class ConfigurableMetricRegistry : MetricRegistry() {
    private val HISTOGRAMS: MetricBuilder<Histogram> = object : MetricBuilder<Histogram> {
        override fun newMetric(): Histogram {
            return Histogram(SlidingTimeWindowArrayReservoir(10, TimeUnit.SECONDS))
        }

        override fun isInstance(metric: Metric?): Boolean {
            return metric is Histogram
        }
    }
    private val TIMERS: MetricBuilder<Timer> = object : MetricBuilder<Timer> {
        override fun newMetric(): Timer {
            return Timer(SlidingTimeWindowArrayReservoir(10, TimeUnit.SECONDS))
        }

        override fun isInstance(metric: Metric?): Boolean {
            return metric is Timer
        }
    }

    override fun histogram(name: String): Histogram {
        return getOrAdd(name, HISTOGRAMS)
    }

    override fun timer(name: String): Timer {
        return getOrAdd(name, TIMERS)
    }

    private fun <T : Metric> getOrAdd(name: String, builder: MetricBuilder<T>): T {
        val metrics: Map<String, Metric> = metrics
        val metric = metrics[name]

        if (metric != null && builder.isInstance(metric)) {
            return metric as T
        }

        if (metric == null) {
            try {
                return register(name, builder.newMetric())
            } catch (e: IllegalArgumentException) {
                val added = metrics[name]
                if (added != null && builder.isInstance(added)) {
                    return added as T
                }
            }
        }

        throw IllegalArgumentException("$name is already used for a different type of metric")
    }

    private interface MetricBuilder<T : Metric> {
        fun newMetric(): T
        fun isInstance(metric: Metric?): Boolean
    }
}
