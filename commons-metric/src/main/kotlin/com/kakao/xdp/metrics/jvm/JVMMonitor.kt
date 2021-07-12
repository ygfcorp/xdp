package com.kakao.xdp.metrics.jvm

import com.codahale.metrics.Gauge
import com.kakao.xdp.metrics.collector.statsd.StatsDMetricCollector
import java.lang.management.ManagementFactory

class JVMMonitor(metricCollector: StatsDMetricCollector) {
    private val METRIC_CATEGORY_NAME_GC = "gc"
    private val METRIC_CATEGORY_NAME_MEMORY = "jvmMemory"

    init {
        // GC
        for (mb in ManagementFactory.getGarbageCollectorMXBeans()) {
            metricCollector.gauge(METRIC_CATEGORY_NAME_GC + '.' + mb.name.toString() + ".count") { Gauge { mb.collectionCount } }
            metricCollector.gauge(METRIC_CATEGORY_NAME_GC + '.' + mb.name.toString() + ".time") { Gauge { mb.collectionTime } }
        }

        // Memory pool
        for (mb in ManagementFactory.getMemoryPoolMXBeans()) {
            metricCollector.gauge(METRIC_CATEGORY_NAME_MEMORY + '.' + mb.name + ".used") { Gauge { mb.usage.used } }
            metricCollector.gauge(METRIC_CATEGORY_NAME_MEMORY + '.' + mb.name + ".committed") { Gauge { mb.usage.committed } }
            metricCollector.gauge(METRIC_CATEGORY_NAME_MEMORY + '.' + mb.name + ".max") { Gauge { mb.usage.max } }
        }
    }
}