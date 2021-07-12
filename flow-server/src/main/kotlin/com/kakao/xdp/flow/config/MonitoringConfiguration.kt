package com.kakao.xdp.flow.config

import com.kakao.xdp.flow.env.ApplicationConfig
import com.kakao.xdp.metrics.collector.statsd.StatsDMetricCollector
import com.kakao.xdp.metrics.jvm.JVMMonitor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MonitoringConfiguration {

    @Bean
    fun statsDMetricCollector(applicationConfig: ApplicationConfig) = applicationConfig.monitoring.let {
        StatsDMetricCollector("flow-server", applicationConfig.phase.name, it.host, it.port, true)
    }

    @Bean
    fun jvmMonitor(statsDMetricCollector: StatsDMetricCollector) = JVMMonitor(statsDMetricCollector)
}
