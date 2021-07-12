package com.kakao.xdp.flow.domain.flow

import com.kakao.xdp.flow.domain.AbstractRepositoryTest
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

class FlowRepositoryTest(@Autowired private val cut: FlowRepository) : AbstractRepositoryTest() {

    @Test
    fun insertAndFind() {
        val now = LocalDateTime.now()
        val flow = Flow(
            name = "poc",
            sourceType = SourceType.BIG_QUERY,
            sourceConnectionInfo = "bigquery://",
            sourceTable = "session",
            sourceColumns = "*",
            destinationType = DestinationType.S3,
            destinationConnectionInfo = "secret",
            createdAt = now,
            updatedAt = now
        )

        cut.insert(flow)

        val found = cut.findById(flow.id)

        assertNotNull(found)
    }
}