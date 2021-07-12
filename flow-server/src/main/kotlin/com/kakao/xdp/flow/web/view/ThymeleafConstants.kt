package com.kakao.xdp.flow.web.view

import com.kakao.xdp.flow.env.ApplicationConfig

data class ThymeleafConstants(val nifiPath: String) {
    fun asMap(): Map<String, Any> {
        return mapOf("nifiPath" to nifiPath)
    }

    companion object {
        fun from(applicationConfig: ApplicationConfig): ThymeleafConstants {
            return ThymeleafConstants(applicationConfig.nifi.ui)
        }
    }
}