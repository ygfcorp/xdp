package com.kakao.xdp.flow.env

import com.kakao.xdp.commons.env.AbstractApplicationConfig
import com.kakao.xdp.commons.env.DBConfig
import com.kakao.xdp.commons.env.Phase
import com.kakao.xdp.commons.server.env.ServerConfig
import com.kakao.xdp.metrics.env.MonitoringConfig

data class ApplicationConfig(val phase: Phase,
                             val server: ServerConfig,
                             val monitoring: MonitoringConfig,
                             val db: DBConfig,
                             val nifi: NifiConfig
) : AbstractApplicationConfig() {
    companion object {
        fun load(phase: Phase): ApplicationConfig = load(phase, "application.yml", ApplicationConfig::class.java)
    }
}

data class NifiConfig(val headless: String,
                      val ui: String)