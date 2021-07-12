package com.kakao.xdp.commons.server.infra.undertow

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.kakao.xdp.commons.env.Phase
import com.kakao.xdp.commons.logging.logger
import com.kakao.xdp.commons.server.env.ServerConfig
import com.kakao.xdp.commons.server.infra.health.HealthSwitcher
import com.kakao.xdp.commons.server.web.servlet.XDPServletInitializer
import io.undertow.Undertow
import io.undertow.UndertowOptions
import io.undertow.server.HttpHandler
import io.undertow.servlet.Servlets
import io.undertow.servlet.api.DeploymentManager
import io.undertow.servlet.api.ServletContainerInitializerInfo
import io.undertow.servlet.util.ImmediateInstanceFactory
import io.undertow.util.ChainedHandlerWrapper
import org.slf4j.Logger
import org.springframework.web.context.WebApplicationContext
import org.xnio.nio.XdpXNioWorker
import java.nio.charset.StandardCharsets
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.function.Supplier
import javax.servlet.ServletContainerInitializer
import javax.servlet.ServletContext
import javax.servlet.ServletException

class UndertowServer(
    private val phase: Phase,
    private val componentName: String,
    private val rootApplicationContext: WebApplicationContext,
    configuration: Supplier<Class<*>>,
    private val serverConfig: ServerConfig
) : AutoCloseable {
    private val logger: Logger = logger(UndertowServer::class.java)

    private val ioThreads = Math.max(Runtime.getRuntime().availableProcessors(), 2)
    private val workerThreads = ioThreads * 8

    private val webMvcInitializer: XDPServletInitializer = XDPServletInitializer(rootApplicationContext, configuration.get())
    private val servletContainerInitializer: ServletContainerInitializer = ServletContainerInitializer { _: Set<Class<*>?>, ctx: ServletContext ->
        webMvcInitializer.onStartup(ctx)
    }
    private val executor: ThreadPoolExecutor = ThreadPoolExecutor(
        100, 100, 60L, TimeUnit.SECONDS, ArrayBlockingQueue(1000),
        ThreadFactoryBuilder().setNameFormat("flow-server-worker-%d").build()
    ).apply { prestartAllCoreThreads() }

    private val deploymentManager: DeploymentManager = Servlets.defaultContainer()
        .addDeployment(
            Servlets.deployment()
                .setDeploymentName("webapp")
                .setContextPath("/")
                .setDefaultEncoding(StandardCharsets.UTF_8.name())
                .setClassLoader(javaClass.classLoader)
                .setExecutor(executor)
                .addServletContainerInitializer(
                    ServletContainerInitializerInfo(
                        ServletContainerInitializer::class.java,
                        ImmediateInstanceFactory(servletContainerInitializer),
                        setOf()
                    )
                )
        )
    private var server: Undertow? = null
    private val worker: XdpXNioWorker = XdpXNioWorker.createWorker(ioThreads, workerThreads)

    private val healthSwitcher: HealthSwitcher = rootApplicationContext.getBean("healthSwitcher", HealthSwitcher::class.java)

    @Throws(Exception::class)
    fun start() {
        deploymentManager.deploy()

        server = Undertow.builder()
            .addHttpListener(
                serverConfig.port,
                "0.0.0.0",
                GracefulShutdownHandler(healthSwitcher, applyChainedHandler(deploymentManager.start()))
            )
            .setWorker<Any>(worker)
            .setServerOption(UndertowOptions.ENABLE_STATISTICS, true)
            .setServerOption(UndertowOptions.DECODE_URL, true)
            .setServerOption(UndertowOptions.URL_CHARSET, "UTF-8")
            .build()
            .apply { start() }

        logger.info("{} Server started with port: {}, phase: {}", componentName, serverConfig.port, phase)
    }

    private fun applyChainedHandler(initialHandler: HttpHandler): HttpHandler {
        val chainedHandlerWrapper: ChainedHandlerWrapper = rootApplicationContext.getBean(
            "chainedHandlerWrapper",
            ChainedHandlerWrapper::class.java
        )
        return chainedHandlerWrapper.wrap(initialHandler)
    }

    @Throws(ServletException::class)
    override fun close() {
        healthSwitcher.goingDown()

        try {
            TimeUnit.SECONDS.sleep(10)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }

        server!!.stop()
        executor.shutdown()
        worker.shutdown()
        deploymentManager.stop()
        deploymentManager.undeploy()
    }
}
