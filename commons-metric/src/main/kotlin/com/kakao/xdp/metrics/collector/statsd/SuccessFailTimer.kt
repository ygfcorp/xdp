package com.kakao.xdp.metrics.collector.statsd

import com.codahale.metrics.Timer
import java.util.concurrent.TimeUnit

class SuccessFailTimer(private val succeededTimer: Timer, private val failedTimer: Timer) {
    fun succeeded(duration: Long, timeUnit: TimeUnit) = succeededTimer.update(duration, timeUnit)

    fun failed(duration: Long, timeUnit: TimeUnit) = failedTimer.update(duration, timeUnit)

    fun occur(duration: Long, timeUnit: TimeUnit, failed: Throwable?) {
        if (failed == null) succeeded(duration, timeUnit) else failed(duration, timeUnit)
    }
}
