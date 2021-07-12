package com.kakao.xdp.flow.domain.recipe

class RecipeService(private val recipeRepository: RecipeRepository) {
    fun allRecipes() = recipeRepository.findAll().toList()
}