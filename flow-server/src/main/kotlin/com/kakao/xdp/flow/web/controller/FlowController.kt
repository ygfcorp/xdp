package com.kakao.xdp.flow.web.controller

import com.kakao.xdp.flow.domain.flow.DestinationType
import com.kakao.xdp.flow.domain.flow.Flow
import com.kakao.xdp.flow.domain.flow.FlowService
import com.kakao.xdp.flow.domain.flow.SourceType
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.ZoneId

@RestController
class FlowController(private val flowService: FlowService) {
    @PostMapping("/v1/flow")
    fun register(@RequestBody flowRegisterRequest: FlowRegisterRequest) = FlowResponse.of(flowService.register(flowRegisterRequest))

    @GetMapping("/v1/flows/{flowId}")
    fun flow(@PathVariable flowId: String) = FlowResponse.of(flowService.findOrException(flowId))

    @PostMapping("/v1/flows/{flowId}/execute")
    fun executeFlow(@PathVariable flowId: String): FlowExecuteResponse {
        flowService.execute(flowId)
        return FlowExecuteResponse(flowId)
    }

    @DeleteMapping("/v1/flows/{flowId}")
    fun deleteFlow(@PathVariable flowId: String) = FlowResponse.of(flowService.delete(flowId))
}

data class FlowRegisterRequest(val name: String,
                               val sourceType: SourceType,
                               val sourceConnectionInfo: String,
                               val sourceTable: String,
                               val sourceColumns: String,
                               val destinationType: DestinationType,
                               val destinationConnectionInfo: String) {
    fun toFlow() = LocalDateTime.now(ZoneId.of("UTC")).let {
        Flow(name, sourceType, sourceConnectionInfo, sourceTable, sourceColumns, destinationType, destinationConnectionInfo, it, it)
    }
}

data class FlowResponse(val id: String) {
    companion object {
        fun of(flow: Flow) = FlowResponse(flow.id)
    }
}

data class FlowExecuteResponse(val flowId: String)