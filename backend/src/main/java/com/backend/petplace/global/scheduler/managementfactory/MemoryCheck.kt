package com.backend.petplace.global.scheduler.managementfactory

import java.lang.management.ManagementFactory

object MemoryCheck {
    fun printHeapMemory() {
        val memoryMXBean = ManagementFactory.getMemoryMXBean()
        val heapUsage = memoryMXBean.getHeapMemoryUsage()

        val used = heapUsage.getUsed()
        val commited = heapUsage.getCommitted()
        val max = heapUsage.getMax()
    }
}