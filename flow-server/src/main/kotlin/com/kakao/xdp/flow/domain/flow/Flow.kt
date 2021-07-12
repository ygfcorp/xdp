package com.kakao.xdp.flow.domain.flow

import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.*

fun generateFlowKey() = UUID.randomUUID().toString()

data class Flow(@Id val id: String,
                val name: String,
                val sourceType: SourceType,
                val sourceConnectionInfo: String,
                val sourceTable: String,
                val sourceColumns: String,
                val destinationType: DestinationType,
                val destinationConnectionInfo: String,
                val nifiPGId: String,
                val status: FlowStatus,
                val createdAt: LocalDateTime,
                val updatedAt: LocalDateTime) {
    constructor(name: String,
                sourceType: SourceType,
                sourceConnectionInfo: String,
                sourceTable: String,
                sourceColumns: String,
                destinationType: DestinationType,
                destinationConnectionInfo: String,
                createdAt: LocalDateTime,
                updatedAt: LocalDateTime) : this(
        generateFlowKey(),
        name,
        sourceType,
        sourceConnectionInfo,
        sourceTable,
        sourceColumns,
        destinationType,
        destinationConnectionInfo,
        name,
        FlowStatus.REGISTERED,
        createdAt,
        updatedAt
    )
}

enum class SourceType {
    BIG_QUERY, RDB
}

enum class DestinationType {
    S3
}

enum class FlowStatus {
    REGISTERED,
    READY,
    RUNNING,
    FINISHED,
    FAILED
}