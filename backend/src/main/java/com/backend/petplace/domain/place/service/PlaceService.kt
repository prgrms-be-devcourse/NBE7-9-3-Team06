package com.backend.petplace.domain.place.service

import com.backend.petplace.domain.place.dto.response.PlaceDetailResponse
import com.backend.petplace.domain.place.dto.response.PlaceSearchResponse
import com.backend.petplace.domain.place.entity.Category2Type
import com.backend.petplace.domain.place.repository.PlaceRepository
import com.backend.petplace.global.exception.BusinessException
import com.backend.petplace.global.response.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlaceService(
    private val placeRepository: PlaceRepository
) {

    @Transactional(readOnly = true)
    fun searchPlaces(
        lat: Double,
        lon: Double,
        radiusKm: Int?,
        category2List: List<Category2Type>?,
        keyword: String?
    ): List<PlaceSearchResponse> {
        // radiusKm가 null이면 기본값, 최대값은 30km로 제한
        val rk = (radiusKm ?: DEFAULT_RADIUS_KM).coerceAtMost(MAX_RADIUS_KM)
        val radiusMeters = rk * 1_000

        // 1) 바운딩 박스 계산
        val box = bbox(lat, lon, rk.toDouble())

        // 2) 카테고리2 문자열 목록 (enum.name 사용)
        val cat2: List<String> = category2List
            ?.map { it.name }
            ?: emptyList()

        // 3) 키워드 가공 (null 또는 blank면 null로 통일)
        val normalizedKeyword = keyword
            ?.takeIf { it.isNotBlank() }
            ?.trim()

        // 4) 쿼리 호출
        val rows = placeRepository.searchWithinRadius(
            lat = lat,
            lon = lon,
            minLat = box.minLat,
            maxLat = box.maxLat,
            minLon = box.minLon,
            maxLon = box.maxLon,
            radiusMeters = radiusMeters,
            category2List = cat2,
            category2Count = cat2.size,
            keyword = normalizedKeyword,
            limit = DEFAULT_SIZE,
            offset = 0
        )

        return rows.map { PlaceSearchResponse.from(it) }
    }

    @Transactional(readOnly = true)
    fun getPlaceDetail(placeId: Long): PlaceDetailResponse {
        val place = placeRepository.findById(placeId)
            .orElseThrow { BusinessException(ErrorCode.NOT_FOUND_PLACE) }

        return PlaceDetailResponse.from(place)
    }

    // Kotlin 내부 data class로 BBox 정의
    private data class BBox(
        val minLat: Double,
        val maxLat: Double,
        val minLon: Double,
        val maxLon: Double
    )

    // 바운딩 박스 계산
    private fun bbox(lat: Double, lon: Double, radiusKm: Double): BBox {
        val R = 6371.0 // km
        val dLat = Math.toDegrees(radiusKm / R)
        val dLon = Math.toDegrees(radiusKm / (R * Math.cos(Math.toRadians(lat))))
        return BBox(
            minLat = lat - dLat,
            maxLat = lat + dLat,
            minLon = lon - dLon,
            maxLon = lon + dLon
        )
    }

    companion object {
        private const val DEFAULT_RADIUS_KM = 10
        private const val MAX_RADIUS_KM = 30
        private const val DEFAULT_SIZE = 300
    }
}
