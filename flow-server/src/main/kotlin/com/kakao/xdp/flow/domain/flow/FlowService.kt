package com.kakao.xdp.flow.domain.flow

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.kakao.xdp.commons.logging.logger
import com.kakao.xdp.commons.server.exception.NotFoundException
import com.kakao.xdp.flow.domain.nifi.NifiFlowService
import com.kakao.xdp.flow.domain.nifi.NifiProcessGroupService
import com.kakao.xdp.flow.web.controller.FlowExecuteCallback
import com.kakao.xdp.flow.web.controller.FlowExecuteResult
import com.kakao.xdp.flow.web.controller.FlowRegisterRequest
import org.slf4j.Logger
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class FlowService(private val flowRepository: FlowRepository,
                  private val nifiProcessGroupService: NifiProcessGroupService,
                  private val nifiFlowService: NifiFlowService) {
    private val logger: Logger = logger(FlowService::class.java)

    private val executorService =
        Executors.newFixedThreadPool(10, ThreadFactoryBuilder().setNameFormat("xdp-nifi-pool-%d").setDaemon(true).build())

    fun register(flowRegisterRequest: FlowRegisterRequest): Flow {
        logger.debug("Register request: {}", flowRegisterRequest)

        val flow = flowRegisterRequest.toFlow()
        flowRepository.insert(flow)

        val nifiPGId: String = nifiProcessGroupService.getRootProcessGroup()!!.let {
            val pg = nifiProcessGroupService.createProcessGroup(it.id, flow.name)!!
            logger.debug("succeed to create process group: id={}, url={}", pg.id, pg.uri)
            pg.id
        }

        // NIFI ID 업데이트
        if (flowRepository.updateNifiPGId(flow.id, nifiPGId) != 1) {
            logger.warn("Fail to update Process Group ID. id: {}, pgId: {}", flow.id, nifiPGId)
        }

        // nifi dag stand by
        executorService.submit {
            logger.debug("execute to stand by nifi. flow={}", flow)
            nifiFlowService.standBy(nifiPGId, flow)
            logger.debug("succeed to stand by nifi. flow={}", flow)
        }
        return findOrException(flow.id)
    }

    fun findOrException(id: String): Flow {
        val found = flowRepository.findById(id)
        if (found.isEmpty) {
            throw NotFoundException("No flow with id `$id`")
        }
        return found.get()
    }

    fun execute(id: String) {
        val flow = findOrException(id)
        nifiFlowService.execute(NifiJobRequest.from(flow), flow)
        flowRepository.updateFlowStatus(id, FlowStatus.RUNNING)

    // TODO insert flow history
    }

    fun shutdown() {
        executorService.shutdown()
        if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
            logger.warn("`executorService` could not be shutdown within 2 seconds.")
        }
    }

    fun findAll(): MutableIterable<Flow> = flowRepository.findAll()

    fun postProcess(callback: FlowExecuteCallback) {
        logger.debug("callback is {}", callback)

        val flowId = callback.flowId
        val requestId = callback.requestId
        flowRepository.updateFlowStatus(flowId, if (callback.result == FlowExecuteResult.SUCCEED) FlowStatus.FINISHED else FlowStatus.FAILED)

        // TODO insert flow history
    }

    fun delete(id: String) = findOrException(id).also {
        nifiFlowService.delete(it)
    }

}

data class NifiJobRequest(private val flowId: String,
                          private val dbType: String,
                          private val tableName: String,
                          private val tableId: String,
                          private val partitionSize: Int,
                          private val columnsToReturn: String) {
    companion object {
        fun from(flow: Flow): NifiJobRequest {
            // TODO hardcoding...
            return NifiJobRequest(
                flowId = flow.id,
                dbType = if (flow.sourceType == SourceType.BIG_QUERY) "bq" else "mysql",
                tableName = if (flow.sourceType == SourceType.BIG_QUERY) "ga_sessions" else "customer",
                tableId = flow.sourceTable,
                partitionSize = 10000, // need to change by condition
                columnsToReturn = flow.sourceColumns
            )
        }
    }
}