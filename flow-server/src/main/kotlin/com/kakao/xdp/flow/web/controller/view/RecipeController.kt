package com.kakao.xdp.flow.web.controller.view

import com.kakao.xdp.flow.domain.recipe.RecipeService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class RecipeController(private val recipeService: RecipeService) : BaseViewController() {
    override fun getSidebarCategory() = SidebarCategory.Recipe

    @GetMapping("/recipes")
    fun recipes() = modelAndView("recipe/list", "Recipes").apply {
        addObject("recipes", recipeService.allRecipes())
    }
}