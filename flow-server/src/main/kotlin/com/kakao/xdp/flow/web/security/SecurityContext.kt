package com.kakao.xdp.flow.web.security

class SecurityContext(private val accessToken: String) {
    companion object {
        const val attributeName = "securityContext"
    }

    fun getAccessToken(): String = accessToken
}
