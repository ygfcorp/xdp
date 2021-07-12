package com.kakao.xdp.flow.domain.nifi

import com.kakao.xdp.flow.infra.nifi.NifiApiService

class NifiQueueService(private val nifiApiService: NifiApiService): NifiApiExecutor() {

    // 1. drop request : POST /nifi-api/flowfile-queues/{connectionId}
    // 2. delete : DELETE /nifi-api/flowfile-queues/{connectionId/drop-requests/{dropRequestId}
    fun deleteQueue(connId: String) =
        executeAndGetBody(nifiApiService.createDropRequest(connId))?.let { dropRequestEntity ->
            executeAndGetBody(nifiApiService.deleteQueue(connId, dropRequestEntity.dropRequest.id))
        }
}