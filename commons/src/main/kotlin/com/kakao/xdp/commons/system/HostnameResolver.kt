package com.kakao.xdp.commons.system

import java.net.InetAddress
import java.net.UnknownHostException

val hostname = try {
    InetAddress.getLocalHost().hostName.split("\\.").toTypedArray()[0]
} catch (e: UnknownHostException) {
    "unknown"
}
