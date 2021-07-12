package com.kakao.xdp.flow.config

import com.kakao.xdp.commons.jackson.Jackson
import com.kakao.xdp.commons.server.exception.DefaultHandlerExceptionResolver
import com.kakao.xdp.commons.server.exception.ServerExceptionHandler
import com.kakao.xdp.commons.server.web.filter.ExceptionHandlerFilter
import com.kakao.xdp.commons.server.web.filter.RequestContextFilter
import com.kakao.xdp.flow.web.view.ThymeleafConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.spring5.view.ThymeleafViewResolver
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.time.Duration

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = ["com.kakao.xdp.flow.web.controller"])
class WebConfiguration : WebMvcConfigurer {
    @Autowired
    private lateinit var thymeleafConstants: ThymeleafConstants

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>?>) {
        converters.clear()
        converters.add(MappingJackson2HttpMessageConverter(Jackson.mapper()))
    }

    @Bean
    fun templateResolver() = ClassLoaderTemplateResolver().apply {
        prefix = "templates/"
        suffix = ".html"
        templateMode = TemplateMode.HTML
        isCacheable = true
    }

    @Bean
    fun templateEngine() = SpringTemplateEngine().apply {
        setTemplateResolver(templateResolver())
        enableSpringELCompiler = true
    }

    @Bean
    fun thymeleafViewResolver() = ThymeleafViewResolver().apply {
        order = 0
        templateEngine = templateEngine()
        staticVariables = thymeleafConstants.asMap()
    }

    override fun configureViewResolvers(registry: ViewResolverRegistry) {
        super.configureViewResolvers(registry)

        registry.viewResolver(thymeleafViewResolver())
    }

    @Bean
    fun serverExceptionHandler() = ServerExceptionHandler()

    @Bean
    @Order(-1)
    fun defaultWebExceptionHandler(serverExceptionHandler: ServerExceptionHandler) =
        DefaultHandlerExceptionResolver(serverExceptionHandler)

    @Configuration
    class FilterConfig {
        @Order(1)
        @Bean
        fun exceptionHandlerFilter(serverExceptionHandler: ServerExceptionHandler) =
            ExceptionHandlerFilter(serverExceptionHandler)

        @Order(2)
        @Bean
        fun requestContextFilter() = RequestContextFilter()

        @Order(3)
        @Bean
        fun corsFilter(corsConfigurationSource: CorsConfigurationSource) =
            CorsFilter(UrlBasedCorsConfigurationSource().apply {
                this.registerCorsConfiguration("/**",
                    CorsConfiguration().also {
                        it.addAllowedOrigin("*")
                        it.addAllowedMethod("*")
                        it.addAllowedHeader("*")
                        it.setMaxAge(Duration.ofSeconds(3600))
                    })
            })
    }
}