package com.backend.petplace.global.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "import")
data class ImportProperties(
  val enabled: Boolean,
  val baseUrl: String,
  val serviceKey: String,
  val pageSize: Int,
  val sleepMs: Long,
  val maxInMemoryMb: Int
)
