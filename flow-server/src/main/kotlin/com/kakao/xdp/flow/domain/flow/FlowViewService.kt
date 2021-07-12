package com.kakao.xdp.flow.domain.flow

import com.kakao.xdp.commons.logging.logger
import com.kakao.xdp.flow.infra.nifi.NifiApiService
import org.apache.nifi.web.api.dto.status.ProcessorStatusDTO
import org.slf4j.Logger
import java.time.LocalDateTime

class FlowViewService(private val flowService: FlowService,
                      private val nifiApiClient: NifiApiService) {
    private val logger: Logger = logger(FlowViewService::class.java)

    fun flowsWithStatus(): List<FlowWithStatus> = flowService.findAll().map { flow ->
        val aggregatedProcessorStatus = AggregatedProcessorStatus()
        try {
            nifiApiClient.getProcessGroupProcessors(flow.nifiPGId).execute().body()!!.processors.forEach { processor ->
                aggregatedProcessorStatus.inc(processor.status)
            }
        } catch (e: Exception) {
            logger.warn("Fail to get processors.", e)
        }

        FlowWithStatus(flow.id, flow.name, flow.nifiPGId, flow.status, aggregatedProcessorStatus, flow.updatedAt)
    }
}

data class FlowWithStatus(val id: String,
                          val name: String,
                          val nifiPGId: String,
                          val status: FlowStatus,
                          val aggregatedProcessorStatus: AggregatedProcessorStatus,
                          val updatedAt: LocalDateTime)

class AggregatedProcessorStatus {
    var running = LinkedHashSet<String>()
        private set
    var stopped = LinkedHashSet<String>()
        private set
    var validating = LinkedHashSet<String>()
        private set
    var disabled = LinkedHashSet<String>()
        private set
    var invalid = LinkedHashSet<String>()
        private set

    fun inc(processorStatus: ProcessorStatusDTO) {
        when (processorStatus.runStatus) {
            RunStatus.Running.name -> running.add(processorStatus.name)
            RunStatus.Stopped.name -> stopped.add(processorStatus.name)
            RunStatus.Validating.name -> validating.add(processorStatus.name)
            RunStatus.Disabled.name -> disabled.add(processorStatus.name)
            RunStatus.Invalid.name -> invalid.add(processorStatus.name)
        }
    }
}

enum class RunStatus {
    Running, Stopped, Validating, Disabled, Invalid
}