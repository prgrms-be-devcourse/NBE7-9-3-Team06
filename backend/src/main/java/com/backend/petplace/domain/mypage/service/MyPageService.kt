package com.backend.petplace.domain.mypage.service

import com.backend.petplace.domain.mypage.dto.MyPageUserInfo
import com.backend.petplace.domain.mypage.dto.MyPageUserPets
import com.backend.petplace.domain.mypage.dto.MyPageUserPoints
import com.backend.petplace.domain.mypage.dto.response.MyPageResponse
import com.backend.petplace.domain.pet.repository.PetRepository
import com.backend.petplace.domain.point.repository.PointRepository
import com.backend.petplace.domain.point.type.PointPolicy
import com.backend.petplace.domain.review.dto.response.MyReviewResponse
import com.backend.petplace.domain.review.repository.ReviewRepository
import com.backend.petplace.domain.review.service.S3Service
import com.backend.petplace.domain.user.entity.User
import com.backend.petplace.domain.user.repository.UserRepository
import com.backend.petplace.global.exception.BusinessException
import com.backend.petplace.global.response.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class MyPageService(
    private val userRepository: UserRepository,
    private val pointRepository: PointRepository,
    private val reviewRepository: ReviewRepository,
    private val petRepository: PetRepository,
    private val s3Service: S3Service
) {

    @Transactional(readOnly = true)
    fun myPage(userId: Long): MyPageResponse {

        val user: User = userRepository.findById(userId)
            .orElseThrow { BusinessException(ErrorCode.NOT_FOUND_MEMBER) }

        val today = LocalDate.now()
        val todayPoints = pointRepository.findTodaysPointsSumByUser(user, today)

        val earnablePoints = (PointPolicy.DAILY_LIMIT.value - todayPoints).coerceAtLeast(0)

        val pointDto: List<MyPageUserPoints> =
            pointRepository.findMyPagePointHistory(user)

        val reviewDtoWithS3Path: List<MyReviewResponse> =
            reviewRepository.findMyReviewsWithProjection(user)

        val reviewDto: List<MyReviewResponse> = reviewDtoWithS3Path.map { dto ->
            MyReviewResponse.withFullImageUrl(
                dto = dto,
                fullImageUrl = s3Service.getPublicUrl(dto.imageUrl)
            )
        }

        val petDto: List<MyPageUserPets> =
            petRepository.findByUserWithActivatedPet(user)

        val userDto = MyPageUserInfo.from(
            user = user,
            earnablePoints = earnablePoints,
            totalReviews = reviewDto.size
        )

        return MyPageResponse(
            userInfo = userDto,
            reviews = reviewDto,
            points = pointDto,
            pets = petDto
        )
    }
}
