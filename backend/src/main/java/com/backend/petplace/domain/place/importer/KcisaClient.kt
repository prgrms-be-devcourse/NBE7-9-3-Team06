package com.backend.petplace.domain.place.importer

import com.backend.petplace.domain.place.dto.KcisaDto
import com.backend.petplace.global.config.ImportProperties
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI

@Component
class KcisaClient(
    private val props: ImportProperties,
    @Qualifier("kcisaWebClient")
    private val client: WebClient
) {

    fun fetchPage(pageNo: Int): List<KcisaDto.Item> {
        val uri = URI.create(
            "${props.baseUrl}?serviceKey=${props.serviceKey}&numOfRows=${props.pageSize}&pageNo=$pageNo"
        )

        val root = client.get()
            .uri(uri)
            .retrieve()
            .bodyToMono(KcisaDto::class.java)
            .block()

        val body = root?.response?.body ?: return emptyList()
        val items = body.items ?: return emptyList()
        return items.item ?: emptyList()
    }
}
