package com.backend.petplace.domain.place.dto.response

import com.backend.petplace.domain.place.entity.Category2Type
import com.backend.petplace.domain.place.projection.PlaceSearchRow
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "PlaceSearchResponse", description = "장소 검색 결과 DTO")
data class PlaceSearchResponse(

    @Schema(description = "장소 ID", example = "123")
    val id: Long,

    @Schema(description = "장소명", example = "행복동물병원")
    val name: String,

    @Schema(
        description = "중분류(검색 필터 주요 기준)",
        example = "VET_HOSPITAL",
        implementation = Category2Type::class
    )
    val category2: Category2Type,

    @Schema(description = "위도", example = "37.5665")
    val latitude: Double,

    @Schema(description = "경도", example = "126.9780")
    val longitude: Double,

    @Schema(description = "요청 좌표로부터 거리(미터)", example = "842", minimum = "0")
    val distanceMeters: Int,

    @Schema(description = "평균 평점", example = "4.6", minimum = "0", maximum = "5")
    val averageRating: Double?,

    @Schema(description = "주소", example = "서울특별시 중구 세종대로 110")
    val address: String
) {
    companion object {
        fun from(row: PlaceSearchRow): PlaceSearchResponse =
            PlaceSearchResponse(
                id = row.id,
                name = row.name,
                category2 = Category2Type.valueOf(row.category2),
                latitude = row.latitude,
                longitude = row.longitude,
                distanceMeters = row.distanceMeters,
                averageRating = row.averageRating,
                address = row.address
            )
    }
}
