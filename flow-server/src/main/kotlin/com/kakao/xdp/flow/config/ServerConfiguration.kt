package com.kakao.xdp.flow.config

import com.kakao.xdp.commons.server.infra.health.HealthSwitcher
import com.kakao.xdp.commons.server.infra.undertow.GracefulShutdownHandlerWrapper
import com.kakao.xdp.commons.server.infra.undertow.ProxyPeerAddressHandlerWrapper
import com.kakao.xdp.commons.server.infra.undertow.UndertowServer
import com.kakao.xdp.flow.env.ApplicationConfig
import io.undertow.util.ChainedHandlerWrapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.context.WebApplicationContext

@Configuration
@Import(
    EnvironmentConfiguration::class,
    MonitoringConfiguration::class,
    ServiceConfiguration::class,
    DataSourceConfiguration::class,
    RepositoryConfiguration::class,
    RetrofitConfiguration::class,
    NifiServiceConfiguration::class
)
class ServerConfiguration {
    @Bean(initMethod = "start", destroyMethod = "close")
    fun undertowServer(rootApplicationContext: WebApplicationContext, applicationConfig: ApplicationConfig) =
        UndertowServer(applicationConfig.phase,
            "Flow server",
            rootApplicationContext,
            { WebConfiguration::class.java },
            applicationConfig.server)

    @Bean
    fun chainedHandlerWrapper(healthSwitcher: HealthSwitcher): ChainedHandlerWrapper {
        return ChainedHandlerWrapper(
            listOf(
                ProxyPeerAddressHandlerWrapper(),
                GracefulShutdownHandlerWrapper(healthSwitcher)
            )
        )
    }

    @Bean
    fun healthSwitcher(applicationConfig: ApplicationConfig) = HealthSwitcher(applicationConfig.phase)
}

