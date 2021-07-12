package com.kakao.xdp.commons.okhttp

import com.kakao.xdp.commons.logging.logger
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.Builder
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.Logger
import java.util.concurrent.TimeUnit

private const val DEFAULT_MAX_REQUESTS = 10;
private const val DEFAULT_MAX_REQUESTS_PER_HOST = 10;
private const val DEFAULT_CONNECTION_TIMEOUT: Long = 1000
private const val DEFAULT_READ_TIMEOUT: Long = 3000
private const val DEFAULT_MAX_IDLE_CONNECTIONS = 120
private const val DEFAULT_KEEP_ALIVE_DURATION: Long = 10

val okhttpLogger: Logger = logger(HttpLoggingInterceptor::class.java)

val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()!!
val FORM_URLENCODED = "application/x-www-form-urlencoded; charset=utf-8".toMediaTypeOrNull()!!

val debugHttpLoggingInterceptor = HttpLoggingInterceptor { okhttpLogger.debug(it) }.apply { setLevel(
    HttpLoggingInterceptor.Level.HEADERS) }

class OkHttpClientBuilder(maxRequests: Int = DEFAULT_MAX_REQUESTS, maxRequestsPerHost: Int = DEFAULT_MAX_REQUESTS_PER_HOST) {
    private val builder = defaultBuilder(maxRequests, maxRequestsPerHost)

    companion object {
        fun defaultBuilder(maxRequests: Int, maxRequestsPerHost: Int) = defaultBuilder(maxRequests, maxRequestsPerHost, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_MAX_IDLE_CONNECTIONS, DEFAULT_KEEP_ALIVE_DURATION)

        fun defaultBuilder(maxRequests: Int = DEFAULT_MAX_REQUESTS, maxRequestsPerHost: Int = DEFAULT_MAX_REQUESTS_PER_HOST,
                           connectionTimeout: Long = DEFAULT_CONNECTION_TIMEOUT, readTimeout: Long = DEFAULT_READ_TIMEOUT,
                           maxIdleConnections: Int = DEFAULT_MAX_IDLE_CONNECTIONS, keepAliveDuration: Long = DEFAULT_KEEP_ALIVE_DURATION): Builder {
            return Builder().connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .connectionPool(ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.MINUTES))
                .dispatcher(Dispatcher().apply {
                    this.maxRequests = maxRequests
                    this.maxRequestsPerHost = maxRequestsPerHost
                })
                .addInterceptor(HttpLoggingInterceptor().also {
                    it.level = HttpLoggingInterceptor.Level.BODY
                })
        }
    }

    fun build(): OkHttpClient {
        return builder.build()
    }
}
