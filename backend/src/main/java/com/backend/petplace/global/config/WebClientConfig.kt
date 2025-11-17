package com.backend.petplace.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig(
    private val props: ImportProperties
) {

    @Bean(name = ["kcisaWebClient"])
    fun kcisaWebClient(): WebClient {
        val bytes = props.maxInMemoryMb * 1024 * 1024

        val strategies = ExchangeStrategies.builder()
            .codecs { config -> config.defaultCodecs().maxInMemorySize(bytes) }
            .build()

        return WebClient.builder()
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT_ENCODING, "gzip")
            .exchangeStrategies(strategies)
            .build()
    }
}
