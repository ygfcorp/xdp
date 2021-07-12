package com.kakao.xdp.flow.domain.processor

import com.kakao.xdp.commons.system.randomUUID

data class ProcessorGroup(val id: String,
                          val name: String,
                          val nifiProcessorId: String) {
    constructor(name: String, nifiProcessorId: String) : this(randomUUID(), name, nifiProcessorId)
}