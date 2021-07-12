package com.kakao.xdp.flow.config

import com.kakao.xdp.commons.env.DBConfig
import org.apache.commons.dbcp2.BasicDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataSourceConfiguration {
    @Bean
    fun dataSource(dbConfig: DBConfig) = BasicDataSource().apply {
        url = dbConfig.host
        username = dbConfig.user
        password = dbConfig.password
        driverClassName = "com.mysql.cj.jdbc.Driver"
    }
}