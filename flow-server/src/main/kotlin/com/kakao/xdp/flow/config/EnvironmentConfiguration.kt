package com.kakao.xdp.flow.config

import com.kakao.xdp.commons.env.Phase
import com.kakao.xdp.flow.env.ApplicationConfig
import com.kakao.xdp.flow.web.view.ThymeleafConstants
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EnvironmentConfiguration {
    @Bean
    fun applicationConfig(@Value("\${phase:LOCAL}") phase: String) = phase.let {
        ApplicationConfig.load(Phase.valueOf(it))
    }

    @Bean
    fun dbConfig(applicationConfig: ApplicationConfig) = applicationConfig.db

    @Bean
    fun thymeleafConstants(applicationConfig: ApplicationConfig) = ThymeleafConstants.from(applicationConfig)
}
