package com.kakao.xdp.flow.domain.flow

import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.repository.CrudRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
interface FlowRepository : CrudRepository<Flow, String>, FlowRepositoryCustom

interface FlowRepositoryCustom {
    fun insert(flow: Flow)
    fun updateNifiPGId(id: String, nifiPGId: String): Int
    fun updateFlowStatus(id: String, status: FlowStatus): Int
}

@Repository
class FlowRepositoryImpl(private val jdbcAggregateTemplate: JdbcAggregateTemplate,
                         private val jdbcTemplate: JdbcTemplate) : FlowRepositoryCustom {
    override fun insert(flow: Flow) {
        jdbcAggregateTemplate.insert(flow)
    }

    override fun updateNifiPGId(id: String, nifiPGId: String) =
        jdbcTemplate.update("UPDATE flow SET nifi_pg_id=?, status=? WHERE id=?", nifiPGId, FlowStatus.READY.name, id)

    override fun updateFlowStatus(id: String, status: FlowStatus) =
        jdbcTemplate.update("UPDATE flow SET status=? WHERE id=?", status.name, id)
}