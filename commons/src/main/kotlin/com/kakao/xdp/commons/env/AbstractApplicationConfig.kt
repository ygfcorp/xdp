package com.kakao.xdp.commons.env

import com.fasterxml.jackson.databind.MappingIterator
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.readValues
import com.kakao.xdp.commons.jackson.Jackson
import com.kakao.xdp.commons.logging.logger
import org.slf4j.Logger

abstract class AbstractApplicationConfig {
    companion object {
        private val logger: Logger = logger(AbstractApplicationConfig::class.java)

        fun <T> load(phase: Phase, configFile: String, clazz: Class<T>): T {
            logger.info("Load application config for phase: {}, {}", phase, configFile)

            val `is` = ClassLoader.getSystemResourceAsStream(configFile)

            val parser = Jackson.yamlMapper().createParser(`is`)
            val iter: MappingIterator<ObjectNode> = Jackson.yamlMapper().readValues(parser)
            try {
                return iter.readAll().first { it.get("phase").asText() == phase.name }.let {
                    Jackson.yamlMapper().convertValue(it, clazz)
                }
            } catch (e: Exception) {
                throw IllegalStateException("Fail to load configuration. Die.", e)
            }
        }
    }
}

data class DBConfig(val host: String,
                    val port: Int,
                    val user: String,
                    val password: String)
