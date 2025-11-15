package com.backend.petplace.domain.place.dto.response

import com.backend.petplace.domain.place.entity.Category1Type
import com.backend.petplace.domain.place.entity.Category2Type
import com.backend.petplace.domain.place.entity.Place
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "PlaceDetailResponse", description = "장소 상세 응답 DTO")
data class PlaceDetailResponse(

    @Schema(description = "장소 ID", example = "123")
    val id: Long,

    @Schema(description = "장소명", example = "행복동물병원")
    val name: String,

    @Schema(description = "대분류")
    val category1: Category1Type,

    @Schema(description = "중분류")
    val category2: Category2Type,

    @Schema(description = "영업시간", example = "월~금 09:00~18:00")
    val openingHours: String?,

    @Schema(description = "휴무일", example = "매주 토/일, 공휴일")
    val closedDays: String?,

    @Schema(description = "주차 가능")
    val parking: Boolean?,

    @Schema(description = "반려동물 동반 가능")
    val petAllowed: Boolean?,

    @Schema(description = "반려동물 제한", example = "야외만")
    val petRestriction: String?,

    @Schema(description = "전화번호", example = "031-000-0000")
    val tel: String?,

    @Schema(description = "웹사이트 URL", example = "https://www.naver.com/~")
    val url: String?,

    @Schema(description = "우편번호", example = "06236")
    val postalCode: String?,

    @Schema(description = "주소", example = "~시 ~구 ~로 65")
    val address: String?,

    @Schema(description = "위도", example = "33.0000000")
    val latitude: Double,

    @Schema(description = "경도", example = "126.000000")
    val longitude: Double,

    @Schema(description = "평균 평점", example = "7")
    val averageRating: Double,

    @Schema(description = "리뷰 수", example = "70")
    val totalReviewCount: Int,

    @Schema(
        description = "원본 설명",
        example = "운영시간:~|휴무일 :~|주차 불가|반려동물 동반가능|반려동물 제한사항 :~"
    )
    val rawDescription: String?

) {
    companion object {
        fun from(place: Place): PlaceDetailResponse =
            PlaceDetailResponse(
                id = place.id!!,
                name = place.name,
                category1 = place.category1,
                category2 = place.category2,
                openingHours = place.openingHours,
                closedDays = place.closedDays,
                parking = place.parking,
                petAllowed = place.petAllowed,
                petRestriction = place.petRestriction,
                tel = place.tel,
                url = place.url,
                postalCode = place.postalCode,
                address = place.address,
                latitude = place.latitude,
                longitude = place.longitude,
                averageRating = place.averageRating,
                totalReviewCount = place.totalReviewCount,
                rawDescription = place.rawDescription
            )
    }
}
