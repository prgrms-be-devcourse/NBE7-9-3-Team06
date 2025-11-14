package com.backend.petplace.domain.point.dto

import com.backend.petplace.domain.point.entity.PointDescription
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "개별 포인트 거래 내역 정보")
data class PointTransaction(
    @Schema(description = "포인트 내역 ID", example = "1")
    val pointId: Long,

    @Schema(description = "포인트가 적립된 장소 정보")
    val place: PlaceInfo,

    @Schema(description = "이미지 첨부 여부", example = "true")
    val hasImage: Boolean,

    @Schema(description = "적립 날짜", example = "2025-10-15")
    val createdDate: LocalDate,

    @Schema(description = "적립된 포인트", example = "100")
    val points: Int,

    @Schema(description = "적립 설명", example = "사진 리뷰 작성")
    val description: String
) {
    constructor(
        pointId: Long,
        placeId: Long,
        placeName: String,
        placeAddress: String,
        descriptionEnum: PointDescription,
        rewardDate: LocalDate,
        amount: Int
    ) : this(
        pointId = pointId,
        place = PlaceInfo(placeId, placeName, placeAddress),
        hasImage = (descriptionEnum == PointDescription.REVIEW_PHOTO),
        createdDate = rewardDate,
        points = amount,
        description = descriptionEnum.description
    )
}