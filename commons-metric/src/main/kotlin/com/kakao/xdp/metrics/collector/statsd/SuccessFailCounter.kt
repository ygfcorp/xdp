package com.kakao.xdp.metrics.collector.statsd

import com.codahale.metrics.Counter

class SuccessFailCounter(private val succeededCounter: Counter, private val failedCounter: Counter) {
    fun succeeded() = succeededCounter.inc()

    fun failed() = failedCounter.inc()
}
