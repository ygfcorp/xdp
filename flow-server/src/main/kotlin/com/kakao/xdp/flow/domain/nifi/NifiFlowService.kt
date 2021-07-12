package com.kakao.xdp.flow.domain.nifi

import com.kakao.xdp.commons.logging.logger
import com.kakao.xdp.flow.domain.flow.Flow
import com.kakao.xdp.flow.domain.flow.NifiJobRequest
import com.kakao.xdp.flow.domain.flow.SourceType
import com.kakao.xdp.flow.infra.nifi.NifiApiService
import org.apache.nifi.web.api.entity.ConnectionsEntity
import org.apache.nifi.web.api.entity.ControllerServicesEntity
import org.apache.nifi.web.api.entity.TemplatesEntity
import org.slf4j.Logger
import retrofit2.Response

class NifiFlowService(private val nifiControllerService: NifiControllerService,
                      private val nifiProcessGroupService: NifiProcessGroupService,
                      private val nifiQueueService: NifiQueueService,
                      private val nifiApiClient: NifiApiService) : NifiApiExecutor() {
    private val logger: Logger = logger(NifiFlowService::class.java)

    fun standBy(pgId: String, flow: Flow) {
        importTemplate(pgId, flow)
        enableControllerService(pgId, flow)

        Thread.sleep(5000) // FIXME controller service가 활성화 된 상태에서 pg를 running 상태로 변경해야 한다.
        // UI 상으로, pg 상태 조회 및 다시 running 상태로 요청할 수 있도록 한다.
        changeScheduledState(pgId, ScheduledState.RUNNING)
    }

    fun execute(nifiJobRequest: NifiJobRequest, flow: Flow) {
        val response: Response<Unit>
        try {
            response = nifiApiClient.execute(resolveUrl(flow), nifiJobRequest).execute()
        } catch (e: Exception) {
            logger.error("Fail to request flow execute. flow: {}, req: {}", flow, nifiJobRequest, e)
            throw NifiException("Fail to request flow execute.", e)
        }

        if (!response.isSuccessful) {
            logger.error("Execute request return non 200. flow: {}, req: {}, status: {}", flow, nifiJobRequest, response.code())
            throw NifiException("Fail to request flow execute")
        }
    }

    fun delete(flow: Flow) {
        val pgId = flow.nifiPGId
        // pg stop
        changeScheduledState(pgId, ScheduledState.STOPPED)

        // empty queue
        nifiProcessGroupService.getConnections(pgId)?.let { entity ->
            entity.connections.forEach { connection ->
                nifiQueueService.deleteQueue(connection.id)
                logger.debug("delete queue: flow={}, pg={}, conn={}", flow.id, pgId, connection.id)
            }
        }

        // disable controller service
        getControllerServices(pgId)!!.controllerServices.forEach {
            nifiControllerService.changeStatus(flow.name, it.id, RunStatus.DISABLED)
        }

        // delete pg
        nifiProcessGroupService.deleteProcessGroup(pgId)
    }

    private fun importTemplate(pgId: String, flow: Flow) {
        val requestTemplateName = resolveTemplateName(flow)

        logger.debug("request to import template. flow={}", flow)
        val templatesEntity: TemplatesEntity = getTemplates()!!
        templatesEntity.templates.stream()
            .filter {
                it.template.name == requestTemplateName
            }
            .findFirst().ifPresent { t ->
                nifiProcessGroupService.createInstanceFromTemplate(pgId, t.id)
                logger.debug("succeed to import template. flow={}", flow)
            }
    }

    private fun enableControllerService(pgId: String, flow: Flow) {
        val pgName = flow.name
        val controllerServicesEntity: ControllerServicesEntity =
            getControllerServices(pgId) ?: return

        var dbcpLookupCsId: String? = null
        controllerServicesEntity.controllerServices.forEach {

            // update property
            var propertyMap: Map<String, String>? = null
            if (it.component.name == "BigQueryDBCPConnectionPool" && flow.sourceType == SourceType.BIG_QUERY) {
                propertyMap = mapOf("Database Connection URL" to flow.sourceConnectionInfo)
            } else if (it.component.name == "MySQLDBCPConnectionPool" && flow.sourceType == SourceType.RDB) {
                // TODO add column? for db user
                propertyMap = mapOf(
                    "Database Connection URL" to flow.sourceConnectionInfo,
                    "Database User" to "admin",
                    "Password" to "rptf135!#%"
                )
            }

            propertyMap?.run {
                nifiControllerService.updateProperties(
                    csId = it.id, updateRequest = ControllerServiceUpdateRequest(
                        revision = Revision(pgName),
                        component = ControllerServiceComponent(
                            it.component.name, it.id,
                            this
                        )
                    )
                )
            }

            // enable
            if (it.component.name == "DBCPConnectionPoolLookup") {
                dbcpLookupCsId = it.id
            } else {
                nifiControllerService.changeStatus(pgName, it.id, RunStatus.ENABLED)
            }
        }

        // DBCPConnectionPoolLookup must be enabled finally
        dbcpLookupCsId?.run {
            nifiControllerService.changeStatus(pgName, this, RunStatus.ENABLED)
        }
    }

    private fun resolveTemplateName(flow: Flow) = flow.sourceType.name + "|" + flow.destinationType.name

    private fun getTemplates() = executeAndGetBody(nifiApiClient.getTemplates())

    private fun getControllerServices(pgId: String) = executeAndGetBody(nifiApiClient.getControllerServices(pgId))

    private fun changeScheduledState(pgId: String, state: ScheduledState) =
        executeAndGetBody(nifiApiClient.changeStateOfSchedule(pgId, ScheduledStateRequest(pgId, state)))

    // TODO k8s를 클라이언트마다 띄운다고 가정하고 실행 요청이 들어오면 그걸 각 노드로 라우팅하는 매커니즘이 필요
    private val bqHost = "http://nifi-headless:8082/xdp"
    private val rdbHost = "http://nifi-headless:8083/xdp"

    private fun resolveUrl(flow: Flow) =
        when (flow.sourceType) {
            SourceType.BIG_QUERY -> bqHost
            else -> rdbHost
        }
}

data class ScheduledStateRequest(private val id: String, private val state: ScheduledState)

enum class ScheduledState {
    RUNNING, STOPPED, ENABLED, DISABLED
}