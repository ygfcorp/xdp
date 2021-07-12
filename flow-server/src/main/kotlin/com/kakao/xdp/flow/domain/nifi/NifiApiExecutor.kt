package com.kakao.xdp.flow.domain.nifi

import retrofit2.Call

open class NifiApiExecutor {

    fun <T> executeAndGetBody(call: Call<T>): T? {
        return call.execute().body()
    }
}