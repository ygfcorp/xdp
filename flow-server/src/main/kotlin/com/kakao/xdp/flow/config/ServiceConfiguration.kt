package com.kakao.xdp.flow.config

import com.kakao.xdp.flow.domain.flow.FlowRepository
import com.kakao.xdp.flow.domain.flow.FlowService
import com.kakao.xdp.flow.domain.flow.FlowViewService
import com.kakao.xdp.flow.domain.nifi.NifiFlowService
import com.kakao.xdp.flow.domain.nifi.NifiProcessGroupService
import com.kakao.xdp.flow.domain.recipe.RecipeRepository
import com.kakao.xdp.flow.domain.recipe.RecipeService
import com.kakao.xdp.flow.infra.nifi.NifiApiService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfiguration {

    @Bean(destroyMethod = "shutdown")
    fun flowService(flowRepository: FlowRepository,
                    nifiProcessGroupService: NifiProcessGroupService,
                    nifiFlowService: NifiFlowService)
    = FlowService(flowRepository, nifiProcessGroupService, nifiFlowService)

    @Bean
    fun flowViewService(flowService: FlowService,
                        nifiApiClient: NifiApiService) = FlowViewService(flowService, nifiApiClient)

    @Bean
    fun recipeService(recipeRepository: RecipeRepository) = RecipeService(recipeRepository)
}