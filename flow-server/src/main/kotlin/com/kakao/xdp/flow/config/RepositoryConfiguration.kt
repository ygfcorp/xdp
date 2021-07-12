package com.kakao.xdp.flow.config

import com.kakao.xdp.flow.domain.recipe.RecipeDAG
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource


@Configuration
@EnableJdbcRepositories(basePackages = ["com.kakao.xdp.flow.domain"])
@EnableTransactionManagement
class RepositoryConfiguration : AbstractJdbcConfiguration() {

    @Bean
    fun transactionManager(dataSource: DataSource) = DataSourceTransactionManager(dataSource)

    @Bean
    fun namedParameterJdbcOperations(dataSource: DataSource) = NamedParameterJdbcTemplate(dataSource)

    @Bean
    fun jdbcTemplate(dataSource: DataSource) = JdbcTemplate(dataSource)

    override fun jdbcCustomConversions(): JdbcCustomConversions {
        return JdbcCustomConversions(listOf<Any>(RecipeDAG.RecipeDAGToJsonStringConverter(), RecipeDAG.JsonStringToRecipeDAGConverter()))
    }
}
