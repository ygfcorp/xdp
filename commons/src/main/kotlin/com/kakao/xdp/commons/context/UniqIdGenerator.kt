package com.kakao.xdp.commons.context

import java.util.*

object UniqIdGenerator {

    // TODO : need to define format
    fun getId() = UUID.randomUUID().toString().replace("-", "")
    
}
