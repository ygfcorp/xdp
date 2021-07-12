package com.kakao.xdp.flow.domain.nifi

import com.kakao.xdp.flow.infra.nifi.NifiApiService

class NifiControllerService(private val nifiApiService: NifiApiService) : NifiApiExecutor() {

    fun updateProperties(csId: String, updateRequest: ControllerServiceUpdateRequest) =
        executeAndGetBody(nifiApiService.updateProperties(csId, updateRequest))

    fun changeStatus(clientId: String, csId: String, state: RunStatus) = executeAndGetBody(
        nifiApiService.changeStatus(csId, RunStatusChangeRequest(Revision(clientId), state)))

}

data class RunStatusChangeRequest(private val revision: Revision, private val state: RunStatus)

enum class RunStatus {
    ENABLED, DISABLED
}

data class ControllerServiceUpdateRequest(private val revision: Revision,
                                          private val component: ControllerServiceComponent)

data class ControllerServiceComponent(private val name: String,
                                      private val id: String,
                                      private val properties: Map<String, String>)
