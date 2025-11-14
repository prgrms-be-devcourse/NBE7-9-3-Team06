package com.backend.petplace.global.scheduler.managementfactory

import lombok.extern.slf4j.Slf4j
import java.lang.management.ManagementFactory

@Slf4j
object MemoryCheck {
    fun printHeapMemory() {
        val memoryMXBean = ManagementFactory.getMemoryMXBean()
        val heapUsage = memoryMXBean.getHeapMemoryUsage()

        val used = heapUsage.getUsed()
        val commited = heapUsage.getCommitted()
        val max = heapUsage.getMax()
    }
}