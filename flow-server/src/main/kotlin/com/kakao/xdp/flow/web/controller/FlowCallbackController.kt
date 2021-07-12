package com.kakao.xdp.flow.web.controller

import com.kakao.xdp.flow.domain.flow.FlowService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class FlowCallbackController(private val flowService: FlowService) {

    @PostMapping("/v1/flow/callback")
    fun callbackFlowJob(@RequestBody callback: FlowExecuteCallback) {
        flowService.postProcess(callback)
    }
}

data class FlowExecuteCallback(val flowId: String, val requestId: String, val result: FlowExecuteResult)

enum class FlowExecuteResult {
    SUCCEED, FAILED
}
