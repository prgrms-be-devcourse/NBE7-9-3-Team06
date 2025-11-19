package com.backend.petplace.domain.review.dto

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ReviewInfoTest {

    private fun createDummy(): ReviewInfo {
        return ReviewInfo(
            reviewId = 1L,
            userName = "TestUser",
            content = "ReviewInfoDtoTest입니다.",
            rating = 4,
            imageUrl = "reviews/dummy-image-key.jpg", // S3 Path
            createdDate = LocalDate.now()
        )
    }

    @Test
    @DisplayName("withFullImageUrl: S3 경로를 CloudFront URL로 교체")
    fun withFullImageUrl() {

        // given
        val originalDto = createDummy()
        val expectedCloudFrontUrl = "https://test.cloudfront.net"
        val originalS3Path = originalDto.imageUrl
        val expectedFullImageUrl = expectedCloudFrontUrl + "/" + originalS3Path

        // when
        val resultDto = ReviewInfo.withFullImageUrl(originalDto, expectedFullImageUrl)

        // then
        // 새로운 객체인지 확인
        assertThat(resultDto).isNotSameAs(originalDto)

        assertThat(resultDto.reviewId).isEqualTo(originalDto.reviewId)
        assertThat(resultDto.userName).isEqualTo(originalDto.userName)
        assertThat(resultDto.content).isEqualTo(originalDto.content)
        assertThat(resultDto.rating).isEqualTo(originalDto.rating)

        // URL 교체되었는지 확인
        assertThat(resultDto.imageUrl).isEqualTo(expectedFullImageUrl)
        assertThat(resultDto.createdDate).isEqualTo(LocalDate.now())
    }
}