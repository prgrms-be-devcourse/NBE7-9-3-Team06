package com.backend.petplace.domain.review.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime

@Schema(description = "장소 리뷰 목록에 포함될 개별 리뷰 정보")
data class ReviewInfo (
    @Schema(description = "리뷰 ID", example = "10")
    val reviewId: Long,

    @Schema(description = "작성자 이름", example = "멍멍이집사")
    val userName: String,

    @Schema(description = "리뷰 내용", example = "정말 좋은 곳이었어요! 강아지가 좋아하네요.")
    val content: String,

    @Schema(description = "별점", example = "5")
    val rating: Int,

    @Schema(description = "첨부 이미지 URL (없을 경우 null)", example = "https://s3.bucket/image.jpg")
    val imageUrl: String?,

    @Schema(description = "작성일", example = "2025-10-15")
    val createdDate: LocalDate
) {
    // JPQL Projection용 보조 생성자
    constructor(
        reviewId: Long,
        userName: String,
        content: String,
        rating: Int,
        imageUrl: String?,
        createdDate: LocalDateTime
    ) : this(
        reviewId = reviewId,
        userName = userName,
        content = content,
        rating = rating,
        imageUrl = imageUrl,
        createdDate = createdDate.toLocalDate()
    )

    companion object {
        // 이미지 URL 교체용 유틸리티 메서드 (copy 활용)
        // private constructor를 만들 필요 없이, copy() 메서드로 값만 바꿔서 새 객체 생성
        fun withFullImageUrl(dto: ReviewInfo, fullImageUrl: String?): ReviewInfo {
            return dto.copy(imageUrl = fullImageUrl)
        }
    }
}