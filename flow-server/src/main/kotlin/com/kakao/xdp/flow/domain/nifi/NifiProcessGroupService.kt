package com.kakao.xdp.flow.domain.nifi

import com.kakao.xdp.flow.infra.nifi.NifiApiService
import org.apache.nifi.web.api.entity.ConnectionEntity
import org.apache.nifi.web.api.entity.PortEntity

class NifiProcessGroupService(private val nifiApiService: NifiApiService) : NifiApiExecutor() {

    fun getRootProcessGroup() = executeAndGetBody(nifiApiService.getRootProcessGroup())

    fun getProcessGroup(pgId: String) = executeAndGetBody(nifiApiService.getProcessGroup(pgId))

    fun createProcessGroup(parentPgId: String, pgName: String) = executeAndGetBody(
        nifiApiService.createEmptyProcessGroup(
            parentPgId, ProcessGroupCreateRequest(Revision(clientId = pgName), ProcessGroupComponent(pgName))
        )
    )

    fun createInstanceFromTemplate(pgId: String, templateId: String) =
        executeAndGetBody(nifiApiService.createInstanceFromTemplate(pgId, TemplateInstanceRequest(templateId)))

    fun getOutputPorts(pgId: String) = executeAndGetBody(nifiApiService.getOutputPorts(pgId))

    fun getInputPorts(pgId: String) = executeAndGetBody(nifiApiService.getInputPorts(pgId))

    fun createConnection(clientId: String, parentPgId: String, srcPgId: String, dstPgId: String): ConnectionEntity {
        val outputPort: PortEntity = executeAndGetBody(nifiApiService.getOutputPorts(srcPgId))!!.outputPorts.first()
        val inputPort: PortEntity = executeAndGetBody(nifiApiService.getInputPorts(dstPgId))!!.inputPorts.first()

        return executeAndGetBody(
            nifiApiService.createConnection(pgId = parentPgId, connectionCreateRequest = ConnectionCreateRequest(revision = Revision(clientId),
                                                                 component = ConnectionComponent(
                                                                     source = Connection(id = outputPort.id, groupId = srcPgId, type = outputPort.portType),
                                                                     destination = Connection(id = inputPort.id, groupId = dstPgId, type = inputPort.portType)))))!!
    }

    fun getConnections(pgId: String) = executeAndGetBody(nifiApiService.getConnections(pgId))

    fun deleteProcessGroup(pgId: String) = executeAndGetBody(nifiApiService.deleteProcessGroup(pgId))

}

data class ProcessGroupCreateRequest(private val revision: Revision,
                                     private val component: ProcessGroupComponent)

data class ProcessGroupComponent(private val name: String)

data class TemplateInstanceRequest(private val templateId: String,
                                   private val originX: Double = 0.0,
                                   private val originY: Double = 0.0)

data class ConnectionCreateRequest(private val revision: Revision,
                                   private val component: ConnectionComponent)

data class ConnectionComponent(private val source: Connection, private val destination: Connection)

data class Connection(private val id: String, private val groupId: String, private val type: String)
