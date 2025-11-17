package com.backend.petplace.domain.place.importer

import com.backend.petplace.domain.place.entity.Place
import com.backend.petplace.domain.place.importer.model.ImportParsed
import com.backend.petplace.domain.place.repository.PlaceRepository
import com.backend.petplace.global.config.ImportProperties
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class KcisaImportService(
    private val props: ImportProperties,
    private val client: KcisaClient,
    private val parser: KcisaParser,
    private val placeRepository: PlaceRepository
) {

    /**
     * 전체 페이지를 돌며 적재 (멱등)
     */
    @Transactional
    fun importAll(): Int {
        var total = 0

        for (page in 1..Int.MAX_VALUE) {
            val items = client.fetchPage(page)
            if (items.isEmpty()) break

            for (it in items) {
                val parsed = parser.parse(it)
                upsert(parsed)
                total++
            }

            // 페이징 종료 판단: 응답 아이템 수가 page-size 미만이면 마지막
            if (items.size < props.pageSize) break

            // 다음 호출 전 sleep
            try {
                Thread.sleep(props.sleepMs)
            } catch (_: InterruptedException) {
                // ignored
            }
        }

        return total
    }

    /** uniqueKey 기준 업서트 */
    private fun upsert(f: ImportParsed) {
        val existing = placeRepository.findByUniqueKey(f.uniqueKey)

        if (existing != null) {
            // 수정
            existing.apply(
                name = f.name,
                c1 = f.category1,
                c2 = f.category2,
                openingHours = f.openingHours,
                closedDays = f.closedDays,
                parking = f.parking,
                petAllowed = f.petAllowed,
                petRestriction = f.petRestriction,
                tel = f.tel,
                url = f.url,
                postalCode = f.postalCode,
                address = f.address,
                lat = requireNotNull(f.latitude) { "latitude is null for uniqueKey=${f.uniqueKey}" },
                lng = requireNotNull(f.longitude) { "longitude is null for uniqueKey=${f.uniqueKey}" },
                rawDescription = f.rawDescription
            )
        } else {
            // 신규 저장
            val place = Place(
                id = null,
                uniqueKey = f.uniqueKey,
                name = f.name,
                category1 = f.category1,
                category2 = f.category2,
                openingHours = f.openingHours,
                closedDays = f.closedDays,
                parking = f.parking,
                petAllowed = f.petAllowed,
                petRestriction = f.petRestriction,
                tel = f.tel,
                url = f.url,
                postalCode = f.postalCode,
                address = f.address,
                latitude = requireNotNull(f.latitude) { "latitude is null for uniqueKey=${f.uniqueKey}" },
                longitude = requireNotNull(f.longitude) { "longitude is null for uniqueKey=${f.uniqueKey}" },
                rawDescription = f.rawDescription,
                averageRating = 0.0,
                totalReviewCount = 0
            )
            placeRepository.save(place)
        }
    }
}
