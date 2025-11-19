package com.backend.petplace.domain.review.servcie

import com.backend.petplace.domain.place.entity.Category1Type
import com.backend.petplace.domain.place.entity.Category2Type
import com.backend.petplace.domain.place.entity.Place
import com.backend.petplace.domain.place.repository.PlaceRepository
import com.backend.petplace.domain.point.dto.PlaceInfo
import com.backend.petplace.domain.point.service.PointService
import com.backend.petplace.domain.point.type.PointAddResult
import com.backend.petplace.domain.review.dto.ReviewInfo
import com.backend.petplace.domain.review.dto.request.ReviewCreateRequest
import com.backend.petplace.domain.review.dto.response.MyReviewResponse
import com.backend.petplace.domain.review.entity.Review
import com.backend.petplace.domain.review.repository.ReviewRepository
import com.backend.petplace.domain.review.service.ReviewService
import com.backend.petplace.domain.review.service.S3Service
import com.backend.petplace.domain.user.entity.User
import com.backend.petplace.domain.user.repository.UserRepository
import com.backend.petplace.global.exception.BusinessException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.ArgumentMatchers.anyLong
import java.time.LocalDate
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class ReviewServiceTest {

    private fun <T> anyNonNull(): T {
        return Mockito.any()
    }

    @InjectMocks
    lateinit var reviewService: ReviewService

    @Mock
    lateinit var pointService: PointService

    @Mock
    lateinit var reviewRepository: ReviewRepository

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var placeRepository: PlaceRepository

    @Mock
    lateinit var s3Service: S3Service

    private lateinit var mockUser: User
    private lateinit var mockPlace: Place
    private lateinit var reviewRequest: ReviewCreateRequest
    private lateinit var mockReview: Review

    @BeforeEach
    fun setUp() {
        mockUser = User(
            id = 1L, email = "testuser@example.com", password = "testpassword123",
            nickName = "testUser", address = "서울 강남구", zipcode = "01234",
            addressDetail = "101호"
        )

        mockPlace = Place(
            id = 10L, uniqueKey = "PLACE_UK_1", name = "TestPlace",
            category1 = Category1Type.ETC, category2 = Category2Type.ETC,
            latitude = 37.5, longitude = 127.0, address = "123 Test St"
        )

        mockPlace = spy(mockPlace)

        reviewRequest = ReviewCreateRequest(
            placeId = 10L,
            content = "Good",
            rating = 5,
            s3ImagePath = "reviews/image_test.jpg"
        )

        mockReview = Review(
            id = 10L,
            user = mockUser,
            place = mockPlace,
            content = reviewRequest.content!!,
            rating = reviewRequest.rating!!,
            imageUrl = reviewRequest.s3ImagePath
        )
    }

    @Test
    @DisplayName("리뷰 생성 및 포인트 적립 로직이 정상적으로 실행되어야 한다")
    fun createReview_Success() {
        // given
        `when`(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser))
        `when`(placeRepository.findById(anyLong())).thenReturn(Optional.of(mockPlace))

        `when`(reviewRepository.save(anyNonNull())).thenReturn(mockReview)
        `when`(pointService.addPointsForReview(anyNonNull(), anyNonNull())).thenReturn(PointAddResult.SUCCESS)

        // when
        val response = reviewService.createReview(mockUser.id!!, reviewRequest)

        // then
        assertThat(response.reviewId).isEqualTo(10L)
        assertThat(response.pointResultMessage).isEqualTo(PointAddResult.SUCCESS.message)

        // 핵심 메서드 호출 검증
        verify(reviewRepository, times(1)).save(anyNonNull())
        verify(mockPlace, times(1)).updateReviewStats(mockReview.rating)
        verify(pointService, times(1)).addPointsForReview(mockUser, mockReview)
    }

    @Test
    @DisplayName("리뷰 생성 시 유효하지 않은 사용자 ID면 예외를 발생시켜야 한다")
    fun createReview_UserNotFound_ThrowsException() {
        // given
        `when`(userRepository.findById(anyLong())).thenReturn(Optional.empty())

        // when & then
        assertThrows<BusinessException> { reviewService.createReview(999L, reviewRequest) }

        verify(reviewRepository, never()).save(anyNonNull())
        verify(pointService, never()).addPointsForReview(anyNonNull(), anyNonNull())
    }

    @Test
    @DisplayName("내가 작성한 리뷰 조회 시 CloudFront URL로 변환되어야 한다")
    fun getMyReviews_Success() {
        // given
        val s3Path = "reviews/image.jpg"
        val expectedFullUrl = "https://cdn.cloudfront.net/reviews/image.jpg"

        val dtoWithS3Path = MyReviewResponse(
            reviewId = 1L,
            place = PlaceInfo.from(mockPlace),
            rating = 5,
            content = "Great place!",
            imageUrl = s3Path,
            createdDate = LocalDate.now(),
            pointsAwarded = 100
        )

        `when`(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser))
        `when`(reviewRepository.findMyReviewsWithProjection(anyNonNull()))
            .thenReturn(listOf(dtoWithS3Path))
        `when`(s3Service.getPublicUrl(s3Path)).thenReturn(expectedFullUrl)

        // when
        val results = reviewService.getMyReviews(mockUser.id!!)

        // then
        assertThat(results).hasSize(1)
        assertThat(results[0].imageUrl).isEqualTo(expectedFullUrl)
        verify(s3Service, times(1)).getPublicUrl(s3Path)
    }

    @Test
    @DisplayName("장소별 리뷰 조회 시 CloudFront URL로 변환")
    fun getReviewByPlace_Success() {
        // given
        val s3Path = "reviews/place_image.png"
        val expectedFullUrl = "https://cdn.cloudfront.net/reviews/place_image.png"

        val dtoWithS3Path = ReviewInfo(
            reviewId = 2L,
            userName = "Dummy",
            content = "Test",
            rating = 4,
            imageUrl = s3Path,
            createdDate = LocalDate.now()
        )

        `when`(placeRepository.findById(anyLong())).thenReturn(Optional.of(mockPlace))
        `when`(reviewRepository.findReviewInfosByPlaceWithProjection(anyNonNull()))
            .thenReturn(listOf(dtoWithS3Path))

        `when`(s3Service.getPublicUrl(s3Path)).thenReturn(expectedFullUrl)

        // when
        reviewService.getReviewByPlace(mockPlace.id!!)

        // then
        verify(reviewRepository, times(1)).findReviewInfosByPlaceWithProjection(mockPlace)
        verify(s3Service, times(1)).getPublicUrl(s3Path)
    }
}