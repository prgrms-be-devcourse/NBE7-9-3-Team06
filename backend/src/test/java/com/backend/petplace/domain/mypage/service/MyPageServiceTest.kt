package com.backend.petplace.domain.mypage.service

import com.backend.petplace.domain.mypage.dto.MyPageUserPets
import com.backend.petplace.domain.mypage.dto.MyPageUserPoints
import com.backend.petplace.domain.mypage.dto.response.MyPageResponse
import com.backend.petplace.domain.pet.entity.Gender
import com.backend.petplace.domain.pet.repository.PetRepository
import com.backend.petplace.domain.point.entity.PointDescription
import com.backend.petplace.domain.point.repository.PointRepository
import com.backend.petplace.domain.review.dto.response.MyReviewResponse
import com.backend.petplace.domain.review.repository.ReviewRepository
import com.backend.petplace.domain.review.service.S3Service
import com.backend.petplace.domain.user.entity.User
import com.backend.petplace.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.BDDMockito.given
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class MyPageServiceTest {

    @Mock private lateinit var userRepository: UserRepository
    @Mock private lateinit var pointRepository: PointRepository
    @Mock private lateinit var reviewRepository: ReviewRepository
    @Mock private lateinit var petRepository: PetRepository
    @Mock private lateinit var s3Service: S3Service

    @InjectMocks
    private lateinit var myPageService: MyPageService

    private lateinit var user: User
    private val cloudFrontDomain = "https://cloudfront-test.com" // 테스트 할 때는 직접 하드코딩 한다고 합니다

    @BeforeEach
    fun init() {
        user = User(
            id = 1L,
            nickName = "user1",
            email = "user1@example.com",
            password = "pwd",
            address = "주소",
            addressDetail = "주소2",
            zipcode = "11111"
        )

        userRepository.save(user)
    }

    @Test
    @DisplayName("마이페이지 조회 - earnablePoints 계산 및 DTO 조립 검증")
    fun myPageTest() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.of(user))

        // 오늘 획득한 포인트
        given(pointRepository.findTodaysPointsSumByUser(eq(user), any()))
            .willReturn(900)

        // 포인트 내역
        given(pointRepository.findMyPagePointHistory(user))
            .willReturn(listOf(MyPageUserPoints(1L, PointDescription.REVIEW_PHOTO, 100, LocalDateTime.now())))

        // 리뷰 + S3 URL
        val review = MyReviewResponse(1L, 1L, "장소 이름", "주소", 5, "30자 적은 리뷰 글", "image.png", LocalDateTime.now(), 100)
        given(reviewRepository.findMyReviewsWithProjection(user))
            .willReturn(listOf(review))

        // URL 조합
        given(s3Service.getPublicUrl("image.png"))
            .willReturn("$cloudFrontDomain/image.png")

        // 펫 목록
        given(petRepository.findByUserWithActivatedPet(user))
            .willReturn(listOf(MyPageUserPets(1L, "뚜뚜", Gender.Female, LocalDate.of(2021, 1, 1), "말티즈")))

        // when
        val response: MyPageResponse = myPageService.myPage(1L)

        // then
        // 유저 정보
        assertThat(response.userInfo.id).isEqualTo(1L)
        assertThat(response.userInfo.earnablePoints).isEqualTo(100) // 1000 - 900
        assertThat(response.reviews.size).isEqualTo(1)

        // 리뷰 검증
        val firstReview = response.reviews.first()
        assertThat(firstReview.reviewId).isEqualTo(1L)
        assertThat(firstReview.place.placeId).isEqualTo(1L)
        assertThat(firstReview.pointsAwarded).isEqualTo(100)
        assertThat(firstReview.imageUrl).isEqualTo("$cloudFrontDomain/image.png")

        // 반려동물
        val pet = response.pets.first()
        assertThat(pet.id).isEqualTo(1L)
        assertThat(pet.name).isEqualTo("뚜뚜")
    }
}
