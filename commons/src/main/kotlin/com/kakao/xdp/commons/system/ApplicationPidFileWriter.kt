package com.kakao.xdp.commons.system

import com.kakao.xdp.commons.logging.logger
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.lang.management.ManagementFactory

class ApplicationPidFileWriter(pidFile: String) {
    private val logger: Logger = logger(ApplicationPidFileWriter::class.java)

    private val file = File(pidFile)
    private val pidString = extractPid()
    private var pid = if (StringUtils.isBlank(pidString)) {
        logger.warn("Fail tp get `pid`. Skip to generate pid file.")
        -1
    } else {
        Integer.valueOf(pidString)
    }

    fun write(): Int {
        try {
            write(file, pid)
            logger.info("pid wrote to `{}`. pid : [{}]", file, pid)
        } catch (e: IOException) {
            logger.error("Fail to write pidFile.", e)
        }
        return pid
    }

    /**
     * Below of methods borrowed from org.springframework.boot.ApplicationPid
     */
    @Throws(IOException::class)
    fun write(file: File, pid: Int) {
        file.parentFile?.mkdirs()
        FileWriter(file).use { writer -> writer.append(pid.toString()) }
        file.deleteOnExit()
    }

    private fun extractPid(): String? {
        return try {
            val jvmName = ManagementFactory.getRuntimeMXBean().name
            jvmName.split("@").toTypedArray()[0]
        } catch (ex: Throwable) {
            null
        }
    }
}
