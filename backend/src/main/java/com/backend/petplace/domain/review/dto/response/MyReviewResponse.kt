package com.backend.petplace.domain.review.dto.response

import com.backend.petplace.domain.point.dto.PlaceInfo
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime

@Schema(description = "내 리뷰 목록 조회 응답 DTO")
data class MyReviewResponse(
    @Schema(description = "리뷰 ID", example = "10")
    val reviewId: Long,

    @Schema(description = "리뷰가 달린 장소 정보")
    val place: PlaceInfo,

    @Schema(description = "내가 등록한 별점", example = "4")
    val rating: Int,

    @Schema(description = "리뷰 내용", example = "다음에 또 방문하고 싶네요.")
    val content: String,

    @Schema(description = "첨부한 이미지 URL", example = "https://s3...")
    val imageUrl: String?,

    @Schema(description = "리뷰 작성일", example = "2025-10-15")
    val createdDate: LocalDate,

    @Schema(description = "해당 리뷰로 적립된 포인트", example = "100")
    val pointsAwarded: Int
) {
    // JPQL Projection용 보조 생성자
    constructor(
        reviewId: Long,
        placeId: Long,
        placeName: String,
        placeAddress: String,
        rating: Int,
        content: String,
        imageUrl: String?,
        createdDate: LocalDateTime,
        pointsAwarded: Int? // DB에서 null일 수 있는 값
    ) : this(
        reviewId = reviewId,
        place = PlaceInfo.fromProjection(placeId, placeName, placeAddress),
        rating = rating,
        content = content,
        imageUrl = imageUrl,
        createdDate = createdDate.toLocalDate(),
        pointsAwarded = pointsAwarded ?: 0 // 엘비스 연산자로 null 처리 (null이면 0)
    )

    // 이미지 URL 교체용 유틸리티 메서드 (copy 활용)
    // 기존 객체는 그대로 두고, imageUrl만 바꾼 새 객체를 반환
    companion object {
        @JvmStatic
        fun withFullImageUrl(dto: MyReviewResponse, fullImageUrl: String?): MyReviewResponse {
            return dto.copy(imageUrl = fullImageUrl)
        }
    }
}