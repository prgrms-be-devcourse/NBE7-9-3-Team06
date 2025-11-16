package com.backend.petplace.domain.review.dto.response

import com.backend.petplace.domain.place.entity.Place
import com.backend.petplace.domain.review.dto.ReviewInfo
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "장소 리뷰 목록 응답 DTO")
data class PlaceReviewsResponse(

    @Schema(description = "장소의 평균 별점", example = "4.5")
    val averageRating: Double,

    @Schema(description = "장소의 전체 리뷰 수", example = "120")
    val totalReviewCount: Int,

    @Schema(description = "리뷰 목록")
    val reviews: List<ReviewInfo>
) {
    constructor(place: Place, reviews: List<ReviewInfo>) : this(
        averageRating = place.averageRating,
        totalReviewCount = place.totalReviewCount,
        reviews = reviews
    )
}