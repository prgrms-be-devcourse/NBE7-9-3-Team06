package com.backend.petplace.domain.review.controller

import com.backend.petplace.domain.review.dto.request.ReviewCreateRequest
import com.backend.petplace.domain.review.dto.response.MyReviewResponse
import com.backend.petplace.domain.review.dto.response.PlaceReviewsResponse
import com.backend.petplace.domain.review.dto.response.PresignedUrlResponse
import com.backend.petplace.domain.review.dto.response.ReviewCreateResponse
import com.backend.petplace.domain.review.service.ReviewService
import com.backend.petplace.domain.review.service.S3Service
import com.backend.petplace.global.exception.BusinessException
import com.backend.petplace.global.jwt.CustomUserDetails
import com.backend.petplace.global.response.ApiResponse
import com.backend.petplace.global.response.ErrorCode
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class ReviewController(
    private val reviewService: ReviewService,
    private val s3Service: S3Service
) : ReviewSpecification {

    /**
     * 리뷰 이미지를 업로드할 Presigned URL을 요청하는 API
     */
    @PostMapping("/presigned-url")
    override fun getPresignedUrl(
        @RequestParam filename: String
    ): ResponseEntity<ApiResponse<PresignedUrlResponse?>> {
        val response = s3Service.generatePresignedUrl("reviews", filename)
        return ResponseEntity.ok(ApiResponse.success(response)
        )
    }

    @PostMapping("/reviews")
    override fun createReview(
        @RequestBody request: @Valid ReviewCreateRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails?
    ): ResponseEntity<ApiResponse<ReviewCreateResponse?>> {

        val user = userDetails ?: throw BusinessException(ErrorCode.NOT_LOGIN_ACCESS)
        val currentUserId = user.getUserId()

        val response = reviewService.createReview(currentUserId, request)
        return ResponseEntity.ok(ApiResponse.create(response)
        )
    }

    @GetMapping("/places/{placeId}/reviews")
    override fun getReviewsByPlace(
        @PathVariable placeId: Long
    ): ResponseEntity<ApiResponse<PlaceReviewsResponse?>> {
        val response = reviewService.getReviewByPlace(placeId)
        return ResponseEntity.ok(ApiResponse.success(response)
        )
    }

    @GetMapping("/my/reviews")
    override fun getMyReviews(
        @AuthenticationPrincipal userDetails: CustomUserDetails?
    ): ResponseEntity<ApiResponse<List<MyReviewResponse>?>> {
        val user = userDetails ?: throw BusinessException(ErrorCode.NOT_LOGIN_ACCESS)
        val currentUserId = user.getUserId()

        val myReviews = reviewService.getMyReviews(currentUserId)

        return ResponseEntity.ok(ApiResponse.success(myReviews)
        )
    }
}