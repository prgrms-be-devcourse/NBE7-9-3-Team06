package com.backend.petplace.domain.point.service

import com.backend.petplace.domain.place.entity.Category1Type
import com.backend.petplace.domain.place.entity.Category2Type
import com.backend.petplace.domain.place.entity.Place
import com.backend.petplace.domain.point.repository.PointRepository
import com.backend.petplace.domain.point.type.PointAddResult
import com.backend.petplace.domain.point.type.PointPolicy
import com.backend.petplace.domain.review.entity.Review
import com.backend.petplace.domain.user.entity.User
import com.backend.petplace.domain.user.repository.UserRepository
import com.backend.petplace.global.exception.BusinessException

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness

import java.util.*

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
internal class PointServiceTest {

    private fun <T> anyNonNull(): T {
        return Mockito.any()
    }

    @InjectMocks
    lateinit var pointService: PointService

    @Mock
    lateinit var pointRepository: PointRepository

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var mockUser: User

    private lateinit var mockPlace: Place
    private lateinit var mockReviewWithImage: Review
    private lateinit var mockReviewWithoutImage: Review

    private val DUMMY_USER_ID = 1L
    private val DUMMY_PLACE_ID = 1L
    private val DULT_AMOUNT = 100

    @BeforeEach
    fun setUp() {
        // 2. Place 객체 생성 (Mock이 아니므로 그대로 생성)
        mockPlace = Place(
            id = DUMMY_PLACE_ID, uniqueKey = "UK1", name = "TestPlace",
            category1 = Category1Type.ETC, category2 = Category2Type.ETC,
            latitude = 30.0, longitude = 10.0
        )

        // 3. Review 객체 생성
        mockReviewWithImage = Review(
            id = 1L, user = mockUser, place = mockPlace,
            content = "Review with image.", rating = 5, imageUrl = "key.jpg"
        )
        mockReviewWithoutImage = Review(
            id = 2L, user = mockUser, place = mockPlace,
            content = "Review without image.", rating = 4, imageUrl = null
        )
    }

    @Test
    @DisplayName("1-1. 이미지 리뷰 등록 시 100 포인트 정상 적립")
    fun addPointsForReview_Success_WithImage() {
        // given
        `when`(mockUser.id).thenReturn(DUMMY_USER_ID)
        `when`(mockUser.totalPoint).thenReturn(DULT_AMOUNT)

        val expectedAmount = PointPolicy.REVIEW_PHOTO_POINTS.value

        // Mockito Stubbing (이제 @BeforeEach 밖에서 호출되므로 오류가 해결됨)
        `when`(pointRepository.existsByUserAndPlaceAndRewardDate(anyNonNull(), anyNonNull(), anyNonNull()))
            .thenReturn(false)
        `when`(pointRepository.findTodaysPointsSumByUser(anyNonNull(), anyNonNull())).thenReturn(0)

        // when
        val result = pointService.addPointsForReview(mockUser, mockReviewWithImage)

        // then
        assertThat(result).isEqualTo(PointAddResult.SUCCESS)
        verify(pointRepository, times(1)).save(anyNonNull())
        verify(mockUser, times(1)).addPoints(expectedAmount)
    }

    @Test
    @DisplayName("1-2. 텍스트 리뷰 등록 시 50 포인트가 정상 적립")
    fun addPointsForReview_Success_WithoutImage() {
        // given
        val expectedAmount = PointPolicy.REVIEW_TEXT_POINTS.value

        `when`(pointRepository.existsByUserAndPlaceAndRewardDate(anyNonNull(), anyNonNull(), anyNonNull()))
            .thenReturn(false)
        `when`(pointRepository.findTodaysPointsSumByUser(anyNonNull(), anyNonNull())).thenReturn(0)

        // when
        val result = pointService.addPointsForReview(mockUser, mockReviewWithoutImage)

        // then
        assertThat(result).isEqualTo(PointAddResult.SUCCESS)

        verify(pointRepository, times(1)).save(anyNonNull())
        verify(mockUser, times(1)).addPoints(expectedAmount)
    }

    @Test
    @DisplayName("1-3. 이미 당일 적립된 장소의 리뷰는 ALREADY_AWARDED 반환")
    fun addPointsForReview_AlreadyAwarded() {
        // given
        `when`(pointRepository.existsByUserAndPlaceAndRewardDate(anyNonNull(), anyNonNull(), anyNonNull()))
            .thenReturn(true)

        // when
        val result = pointService.addPointsForReview(mockUser, mockReviewWithImage)

        // then
        assertThat(result).isEqualTo(PointAddResult.ALREADY_AWARDED)

        verify(pointRepository, never()).save(anyNonNull())
        verify(mockUser, never()).addPoints(anyInt())
    }

    @Test
    @DisplayName("1-4. 일일 적립 한도 초과 시 DAILY_LIMIT_EXCEEDED 반환")
    fun addPointsForReview_DailyLimitExceeded() {
        // given
        val dailyLimit = PointPolicy.DAILY_LIMIT.value

        `when`(pointRepository.existsByUserAndPlaceAndRewardDate(anyNonNull(), anyNonNull(), anyNonNull()))
            .thenReturn(false)

        // 한도 초과 상황 설정
        `when`(pointRepository.findTodaysPointsSumByUser(anyNonNull(), anyNonNull())).thenReturn(dailyLimit)

        // when
        val result = pointService.addPointsForReview(mockUser, mockReviewWithImage)

        // then
        assertThat(result).isEqualTo(PointAddResult.DAILY_LIMIT_EXCEEDED)

        verify(pointRepository, never()).save(anyNonNull())
        verify(mockUser, never()).addPoints(anyInt())
    }

    @Test
    @DisplayName("2-1. 포인트 내역 조회 시 응답 DTO 정상적 생성")
    fun getPointHistory_Success() {
        // given
        `when`(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser))
        `when`(pointRepository.findPointHistoryByUser(anyNonNull())).thenReturn(mutableListOf())

        // when
        pointService.getPointHistory(mockUser.id!!)

        verify(userRepository, times(1)).findById(mockUser.id)
        verify(pointRepository, times(1)).findPointHistoryByUser(mockUser)
    }

    @Test
    @DisplayName("2-2. 포인트 내역 조회 시 사용자가 없으면 예외 발생")
    fun getPointHistory_UserNotFound() {
        // given
        `when`(userRepository.findById(anyLong())).thenReturn(Optional.empty())

        // when & then
        assertThrows<BusinessException> { pointService.getPointHistory(999L) }

        // verify (예외 발생했으므로 findPointHistoryByUser는 호출되지 않아야 함)
        verify(pointRepository, never()).findPointHistoryByUser(anyNonNull())
    }
}