package com.kakao.xdp.flow.config

import com.google.gson.*
import com.kakao.xdp.commons.okhttp.OkHttpClientBuilder
import com.kakao.xdp.flow.env.ApplicationConfig
import com.kakao.xdp.flow.infra.nifi.NifiApiService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Configuration
class RetrofitConfiguration {

    @Bean
    fun nifiApiClient(applicationConfig: ApplicationConfig) = Retrofit.Builder()
        .baseUrl(applicationConfig.nifi.headless)
        .client(OkHttpClientBuilder.defaultBuilder().build())
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .registerTypeAdapter(Date::class.java, MultiDateDeserializer())
                    .create()
            )
        )
        .build()
        .create(NifiApiService::class.java)
}

class MultiDateDeserializer : JsonDeserializer<Date> {
    private val formats = arrayOf("HH:mm:ss z", "MM/dd/yyyy HH:mm:ss z")

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext): Date {
        for (df in formats) {
            try {
                return SimpleDateFormat(df).parse(json!!.asString)
            } catch (e: ParseException) {
                // noop
            }
        }
        throw JsonParseException("occurred exception during parse : " + json!!.asString)
    }
}