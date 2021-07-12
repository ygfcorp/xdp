package com.kakao.xdp.flow.web.controller.view

import org.springframework.web.servlet.ModelAndView

abstract class BaseViewController {
    protected fun modelAndView(viewName: String, pageTitle: String) = ModelAndView(viewName).apply {
        addObject("pageTitle", pageTitle)
        addObject("sidebarCategory", getSidebarCategory().name)
    }

    abstract fun getSidebarCategory(): SidebarCategory
}

enum class SidebarCategory {
    Main, Recipe, Flow
}