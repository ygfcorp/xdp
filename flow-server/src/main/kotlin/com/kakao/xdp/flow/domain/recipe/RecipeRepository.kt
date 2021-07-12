package com.kakao.xdp.flow.domain.recipe

import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.repository.CrudRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
interface RecipeRepository : CrudRepository<Recipe, String>, RecipeRepositoryCustom

interface RecipeRepositoryCustom {
    fun insert(recipe: Recipe)
}

@Repository
class RecipeRepositoryImpl(private val jdbcAggregateTemplate: JdbcAggregateTemplate,
                           private val jdbcTemplate: JdbcTemplate) : RecipeRepositoryCustom {
    override fun insert(recipe: Recipe) {
//        jdbcTemplate.update("INSERT INTO recipe (id, name, dag, description, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)", *recipe.toVararg())
        jdbcAggregateTemplate.insert(recipe)
    }
}