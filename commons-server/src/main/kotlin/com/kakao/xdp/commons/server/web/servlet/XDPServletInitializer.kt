package com.kakao.xdp.commons.server.web.servlet

import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter
import org.springframework.web.filter.DelegatingFilterProxy
import org.springframework.web.servlet.FrameworkServlet
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer
import java.nio.charset.StandardCharsets
import javax.servlet.Filter

class XDPServletInitializer(private val rootApplicationContext: WebApplicationContext,
                            private val configurationClass: Class<*>) : AbstractDispatcherServletInitializer() {

    private var servletApplicationContext: WebApplicationContext? = null

    override fun createDispatcherServlet(servletAppContext: WebApplicationContext): FrameworkServlet {
        return XDPDispatcherServlet(servletAppContext)
    }

    override fun createRootApplicationContext(): WebApplicationContext {
        return rootApplicationContext
    }

    override fun createServletApplicationContext(): WebApplicationContext {
        val servletApplicationContext = AnnotationConfigWebApplicationContext()
        servletApplicationContext.register(configurationClass)
        this.servletApplicationContext = servletApplicationContext
        return servletApplicationContext
    }

    override fun getServletMappings(): Array<String> {
        return arrayOf("/")
    }

    override fun getServletFilters(): Array<Filter> {
        return arrayOf(
            CharacterEncodingFilter(StandardCharsets.UTF_8.name()),
            DelegatingFilterProxy("requestContextFilter", servletApplicationContext),
            DelegatingFilterProxy("exceptionHandlerFilter", servletApplicationContext),
        )
    }
}
