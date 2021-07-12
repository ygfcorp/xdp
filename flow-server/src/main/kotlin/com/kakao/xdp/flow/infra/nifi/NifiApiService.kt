package com.kakao.xdp.flow.infra.nifi

import com.kakao.xdp.flow.domain.flow.NifiJobRequest
import com.kakao.xdp.flow.domain.nifi.*
import org.apache.nifi.web.api.entity.*
import retrofit2.Call
import retrofit2.http.*

interface NifiApiService {

    /**
     * 프로세스 그룹(DAG) 조회, 생성, 템플릿으로부터 인스턴스(flow) 생성
     */
    @GET("/nifi-api/process-groups/root")
    fun getRootProcessGroup(): Call<ProcessGroupEntity>

    @GET("/nifi-api/process-groups/{pgId}")
    fun getProcessGroup(@Path("pgId") pgId: String): Call<ProcessGroupEntity>

    @POST("/nifi-api/process-groups/{parentPgId}/process-groups")
    fun createEmptyProcessGroup(@Path("parentPgId") parentPgId: String,
                                @Body processGroupCreateRequest: ProcessGroupCreateRequest): Call<ProcessGroupEntity>

    @POST("/nifi-api/process-groups/{pgId}/template-instance")
    fun createInstanceFromTemplate(@Path("pgId") pgId: String,
                                   @Body templateInstance: TemplateInstanceRequest): Call<FlowEntity>

    @GET("/nifi-api/process-groups/{pgId}/processors")
    fun getProcessGroupProcessors(@Path("pgId") pgId: String): Call<ProcessorsEntity>

    @GET("/nifi-api/process-groups/{pgId}/output-ports")
    fun getOutputPorts(@Path("pgId") pgId: String): Call<OutputPortsEntity>

    @GET("/nifi-api/process-groups/{pgId}/input-ports")
    fun getInputPorts(@Path("pgId") pgId: String): Call<InputPortsEntity>

    @POST("/nifi-api/process-groups/{pgId}/connections")
    fun createConnection(@Path("pgId") pgId: String,
                         @Body connectionCreateRequest: ConnectionCreateRequest): Call<ConnectionEntity>

    @GET("/nifi-api/process-groups/{pgId}/connections")
    fun getConnections(@Path("pgId") pgId: String): Call<ConnectionsEntity>

    @DELETE("/nifi-api/process-groups/{pgId}")
    fun deleteProcessGroup(@Path("pgId") pgId: String): Call<ProcessGroupEntity>

    /**
     * 템플릿 조회
     */
    @GET("/nifi-api/flow/templates")
    fun getTemplates(): Call<TemplatesEntity>


    /**
     * 프로세스 그룹의 컨트롤러 서비스 조회 및 상태 변경
     */
    @GET("/nifi-api/flow/process-groups/{pgId}/controller-services")
    fun getControllerServices(@Path("pgId") pgId: String) : Call<ControllerServicesEntity>

    @PUT("/nifi-api/controller-services/{csId}/run-status")
    fun changeStatus(@Path("csId") csId: String,
                     @Body runStatusChangeRequest: RunStatusChangeRequest): Call<ControllerServiceEntity>

    @PUT("/nifi-api/controller-services/{csId}")
    fun updateProperties(@Path("csId") csId: String,
                         @Body updateRequest: ControllerServiceUpdateRequest): Call<ControllerServiceEntity>

    /**
     * 프로세스 그룹 (스케줄) 실행 변경
     */
    @PUT("/nifi-api/flow/process-groups/{pgId}")
    fun changeStateOfSchedule(@Path("pgId") pgId: String,
                              @Body scheduledStateRequest: ScheduledStateRequest): Call<ScheduleComponentsEntity>

    /**
     * execute trigger : ListenHttp
     */
    @POST
    fun execute(@Url url: String, @Body nifiJobRequest: NifiJobRequest): Call<Unit>

    /**
     * QUEUE
     */
    @POST("/nifi-api/flowfile-queues/{connId}")
    fun createDropRequest(@Path("connId") connId: String) : Call<DropRequestEntity>

    @DELETE("/nifi-api/flowfile-queues/{connId}/drop-requests/{dropRequestId}")
    fun deleteQueue(@Path("connId") connId: String, @Path("dropRequestId") dropRequestId: String): Call<DropRequestEntity>
}