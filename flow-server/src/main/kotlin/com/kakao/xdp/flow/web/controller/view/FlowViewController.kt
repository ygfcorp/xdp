package com.kakao.xdp.flow.web.controller.view

import com.kakao.xdp.flow.domain.flow.FlowViewService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

@Controller
class FlowViewController(private val flowViewService: FlowViewService) : BaseViewController() {
    @GetMapping("/flow/register")
    fun registerView(@RequestParam recipeId: String): ModelAndView {
        return modelAndView("flow/register", "Register Flow").apply {
            addObject("recipeId", recipeId)
        }
    }

    @GetMapping("/flows")
    fun flowView(): ModelAndView {
        return modelAndView("flow/list", "Flows").apply {
            addObject("flows", flowViewService.flowsWithStatus())
        }
    }

    override fun getSidebarCategory() = SidebarCategory.Flow
}