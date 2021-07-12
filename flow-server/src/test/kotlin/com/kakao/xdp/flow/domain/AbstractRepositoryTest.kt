package com.kakao.xdp.flow.domain

import com.kakao.xdp.flow.config.RepositoryConfiguration
import org.apache.commons.dbcp2.BasicDataSource
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import javax.sql.DataSource

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [RepositoryConfiguration::class, TestDataSourceConfiguration::class])
@Transactional
abstract class AbstractRepositoryTest

@Configuration
class TestDataSourceConfiguration {
    @Bean
    fun dataSource() = BasicDataSource().apply {
        url = "jdbc:log4jdbc:mysql://xdp-poc.crg7x6va7xzx.ap-northeast-2.rds.amazonaws.com/xdp_poc"
        username = "admin"
        password = "rptf135!#%"
        driverClassName = "net.sf.log4jdbc.DriverSpy"
    }
}