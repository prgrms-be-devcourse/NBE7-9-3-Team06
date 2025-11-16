package com.backend.petplace.domain.review.service

import com.backend.petplace.domain.place.entity.Place
import com.backend.petplace.domain.place.repository.PlaceRepository
import com.backend.petplace.domain.point.service.PointService
import com.backend.petplace.domain.review.dto.ReviewInfo
import com.backend.petplace.domain.review.dto.request.ReviewCreateRequest
import com.backend.petplace.domain.review.dto.response.MyReviewResponse
import com.backend.petplace.domain.review.dto.response.PlaceReviewsResponse
import com.backend.petplace.domain.review.dto.response.ReviewCreateResponse
import com.backend.petplace.domain.review.entity.Review
import com.backend.petplace.domain.review.repository.ReviewRepository
import com.backend.petplace.domain.user.entity.User
import com.backend.petplace.domain.user.repository.UserRepository
import com.backend.petplace.global.exception.BusinessException
import com.backend.petplace.global.response.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReviewService(
    private val pointService: PointService,
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository,
    private val placeRepository: PlaceRepository,
    private val s3Service: S3Service
) {
    @Transactional
    fun createReview(userId: Long, request: ReviewCreateRequest): ReviewCreateResponse {
        val user = findUserById(userId)
        val place = findPlaceById(request.placeId!!) // DTO에서 검증하지만, 안전하게 처리 (!! 사용)
        val imageUrl = request.s3ImagePath

        val review = Review.createNewReview(
            user = user,
            place = place,
            content = request.content!!,
            rating = request.rating!!,
            imageUrl = imageUrl
        )
        val savedReview = reviewRepository.save(review)
        place.updateReviewStats(savedReview.rating)

        val result = pointService.addPointsForReview(user, savedReview)
        val resultMessage = result.message

        return ReviewCreateResponse(savedReview.id!!, resultMessage)
    }

    @Transactional(readOnly = true)
    fun getMyReviews(currentUserId: Long): List<MyReviewResponse> {
        val user = findUserById(currentUserId)

        val dtosWithS3Path = reviewRepository.findMyReviewsWithProjection(user)

        return dtosWithS3Path.map { dto ->
            MyReviewResponse.withFullImageUrl(
                dto,
                s3Service.getPublicUrl(dto.imageUrl)
            )
        }
    }

    @Transactional(readOnly = true)
    fun getReviewByPlace(placeId: Long): PlaceReviewsResponse {
        val place = findPlaceById(placeId)

        val dtosWithS3Path = reviewRepository.findReviewInfosByPlaceWithProjection(place)

        val dtosWithFullUrl = dtosWithS3Path.map { dto ->
                ReviewInfo.withFullImageUrl(
                    dto,
                    s3Service.getPublicUrl(dto.imageUrl)
                )
            }
        return PlaceReviewsResponse(place, dtosWithFullUrl)
    }

    private fun findUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { BusinessException(ErrorCode.NOT_FOUND_MEMBER) }
    }

    private fun findPlaceById(placeId: Long): Place {
        return placeRepository.findById(placeId)
            .orElseThrow { BusinessException(ErrorCode.NOT_FOUND_PLACE) }
    }
}
