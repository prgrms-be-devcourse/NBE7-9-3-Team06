package com.backend.petplace.domain.point.dto

import com.backend.petplace.domain.place.entity.Place
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "리뷰 응답에 포함될 장소 요약 정보")
data class PlaceInfo(
    @Schema(description = "장소 ID", example = "1")
    val placeId: Long,

    @Schema(description = "장소 이름", example = "멍멍카페")
    val placeName: String,

    @Schema(description = "장소 전체 주소 - 우편번호 제외", example = "서울 강남구 테헤란로 123")
    val fullAddress: String
) {
    companion object {
        fun fromProjection(placeId: Long, placeName: String, address: String): PlaceInfo {
            return PlaceInfo(placeId, placeName, address)
        }

        fun from(place: Place): PlaceInfo {
            return PlaceInfo(
                place.id!!,
                place.name,
                place.address
            )
        }
    }
}