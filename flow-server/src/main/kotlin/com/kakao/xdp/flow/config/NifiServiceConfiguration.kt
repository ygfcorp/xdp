package com.kakao.xdp.flow.config

import com.kakao.xdp.flow.domain.nifi.NifiControllerService
import com.kakao.xdp.flow.domain.nifi.NifiFlowService
import com.kakao.xdp.flow.domain.nifi.NifiProcessGroupService
import com.kakao.xdp.flow.domain.nifi.NifiQueueService
import com.kakao.xdp.flow.infra.nifi.NifiApiService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NifiServiceConfiguration {

    @Bean
    fun nifiProcessGroupService(nifiApiClient: NifiApiService) = NifiProcessGroupService(nifiApiClient)

    @Bean
    fun nifiFlowService(nifiControllerService: NifiControllerService,
                        nifiProcessGroupService: NifiProcessGroupService,
                        nifiQueueService: NifiQueueService,
                        nifiApiClient: NifiApiService) =
        NifiFlowService(nifiControllerService, nifiProcessGroupService, nifiQueueService, nifiApiClient)

    @Bean
    fun nifiControllerService(nifiApiClient: NifiApiService) = NifiControllerService(nifiApiClient)

    @Bean
    fun nifiQueueService(nifiApiClient: NifiApiService) = NifiQueueService(nifiApiClient)
}